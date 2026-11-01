# FEEL 2 — SOUND — THE DRAWER (issue #8)

Feel 2 — Sound (issue #8 cell). Source of law: `studio/plan/AUDIT-drawer.md`
(Lade's verdict — "finished work rests, readable without being judged"),
`studio/work/issue-8/designer-drawer.md` (the floor: one-scroll landing,
MONTHS shelf, filing zone, SEARCH with BACK). Grounding in the code as
built: `DrawerScreen.kt` today makes **no sound call at all** — the
Drawer as shipped is silent — and `service/TactileSoundEngine.kt` is a
procedural float-PCM synthesiser: zero audio files, zero ambient loops,
every voice a named function. This file specifies **only what the
finished Drawer sounds like**. No Kotlin edits. Nobody compiles.

---

## 0 · THE VERDICT IN ONE PARAGRAPH

The default posture **stands, defended**: near-total silence, with
exactly **one** earned sound in the whole room — the filing of a
day-word. Six of the seven events below are **NO SOUND**, ruled
individually, not by laziness. The one survivor survives because it is
the only event in this room that is a *write* rather than a *reading*,
and the only one whose confirmation a man may need without looking at
the screen: Muede's 1 AM errand — Drawer, ILL, sleep (audit §4). Every
other proposed sound was a navigation click, a page rustle, a search
ding, or a stamp — theatre on a browsing surface, or worse, a grade
delivered by ear in the room whose entire law is that nothing grades.

### Verdict table

| # | Event | Sound? |
|---|---|---|
| 1 | Opening the Drawer | **NO** |
| 2 | Filing a day-word (REST / ILL / FAMILY) | **YES — the one sound** (§2) |
| 3 | Shelf row → MONTH | **NO** |
| 4 | Opening a task DETAIL | **NO** |
| 5 | EARLIER paging | **NO** |
| 6 | Search typing / clearing | **NO** |
| 7 | BACK (every parent, incl. SEARCH's) | **NO** |

---

## 1 · WHY SILENCE IS THE ROOM'S NATIVE STATE

Three arguments, in the order they bind:

1. **A sound that repeats is a count.** Anything fired per tap, per
   row, per page, or per keystroke lets the ear tally the archive —
   pages paged, months opened, keys typed. The audit acquitted this
   screen of every printed total (§6); a per-event click reintroduces
   the same tally in a channel no print audit can see. Navigation
   sounds are counts with good manners.
2. **A sound with a valence is a score.** A rising pitch, a bright
   chime, a two-note "done" — the ear reads these as praise before
   the mind reads anything. The completion voice already exists in
   this app and is exactly that (`playCompletionPulse`: three ticks
   softening as they land, 185 Hz, gain 0.68). Spending anything in
   that family inside the archive — on a filing, a found search
   result, an opened detail — congratulates a man for resting, for
   being ill, for finding his own finished work. A success chime in
   the archive room is a score by ear. It does not matter that no
   number prints; the grade was heard.
3. **The 1 AM case binds every event, not just the quiet ones.** The
   design case for this room is a sick man at 01:00 filing ILL in a
   room where someone may be asleep. Any sound specified "for the
   daytime feel" that also fires at 01:00 is a sound that announces
   him. There is no day/night sound split in this spec — a voice
   that is only acceptable when nobody is sleeping is not acceptable.

Against that, one counter-argument, and it wins exactly once: a
*write* that retires a line to permanent print (designer §4) is an
act, not a browse. Acts elsewhere in this app speak — the latch, the
seal, the tear. Readings nowhere speak. The Drawer contains one act.
It gets one sound. That is the whole design.

---

## 2 · THE ONE SOUND — FILING A DAY-WORD

**Event:** a tap on REST, ILL, or FAMILY in the filing zone (today's
line or tomorrow's line) that successfully files that day.

### 2.1 Character, in plain words

A single piece of card set down flat on a wooden desk. One dull,
low **settle** — not a strike, not a slide, not a stamp:

- **Material:** paper/card on wood. Dry, soft-edged, no metal, no
  plastic click, no spring.
- **Length:** ~30–45 ms of body, fully decayed inside ~60 ms. One
  event, one envelope, decay only — nothing sustains, nothing rings,
  there is no second tick and no tail.
- **Weight:** light but final. It should feel like something coming
  to rest in a drawer, not like something being approved, locked, or
  won.
- **Pitch shape:** a single low fundamental, roughly **180–220 Hz**,
  falling energy only. **No interval, no rise, no chord that resolves
  upward.** A sound that goes up is a reward; a sound that settles
  down is a filing. In engine terms this is the existing synth body
  family at a low fundamental with the bright upper partial kept
  *back* — the opposite voicing from the completion pulse.

### 2.2 What it is explicitly NOT — the three rejected candidates

- **Not a paper stamp.** A stamp is the loudest idea in the brief and
  the wrongest. A stamp is percussive, public, and semantically it
  means *approved by an authority* — the app stamping his sick day
  valid is judgement performed as office theatre. It is also the
  one candidate that would carry across a room at 01:00. Killed on
  meaning first, volume second.
- **Not a drawer slide.** A slide is long (hundreds of ms by nature),
  it is a *navigation* sound (things moving past), and filing moves
  nothing past anything — a word lands beside a date and stays.
  Slides belong, if anywhere, to §3/§4 events, where they are also
  refused.
- **Not any existing voice, spent here.** `playSealThrow` (the Throw:
  1000 ms, 360 Hz body + bolt + room tail, gain 0.62, bit-identical,
  golden-hashed) and `playHarmonicSeal` are the seal's voices —
  filing a day does not seal anything, and borrowing the seal's sound
  would both lie about the state and cheapen the one voice the whole
  app protects. `playCompletionPulse` is the finish voice — see §1.2:
  it grades. `playTearPulse` is the counterfoil's. This file asks
  for a new, small, dedicated voice (priced in §5) precisely so no
  other room's meaning leaks into this one.

### 2.3 Why it does not grade

- **Identical for all three words.** REST, ILL, and FAMILY file with
  the *same* sound, today and tomorrow alike — no sadder ILL, no
  softer REST, no warmer FAMILY. The designer already killed semantic
  colour-coding for the words in print (§1 of the designer file:
  "colour-grading a sick day is still grading"); this is the same law
  in the audio channel. The sound encodes *that a day was filed* and
  nothing about *which day it was*.
- **Identical regardless of everything else.** It does not vary with
  the day's SPENT, the number of tasks stored that day, the hour of
  the tap, or whether the filing is early, on time, or late. Like the
  Throw's zero-parameter law, no fact about the day may enter the
  voice — a sound that varied by content would be a report.
- **It confirms a write, it does not evaluate one.** Filing is not an
  achievement and not a failure; REST and ILL are often the opposite
  of achievement, and the app must be able to receive them in the
  same neutral voice as FAMILY. The sound says "stored," the way the
  retiring print line says it — and says nothing else.

### 2.4 Volume and register — placed against the app's seal sounds

| Voice (as built in `TactileSoundEngine`) | Length | Gain | Role |
|---|---|---|---|
| `playSealThrow` | 1000 ms + room tail | 0.62 | The ceiling. Sacred, bit-identical. |
| `playCompletionPulse` | 64 ms | 0.68 | The finish — never in this room. |
| `playTearPulse` | 40 ms | 0.68 | The counterfoil tear. |
| `playHarmonicSeal` | 48 ms | 0.62 | Arming/disarming a session. |
| `playFirmClick` / `playMechanicalLatch` | 12–16 ms | 0.50–0.54 | Heavy controls, the latch. |
| **`playFilingSettle` (this spec, proposed name)** | **~30–45 ms** | **≤ 0.35** | **The Drawer's only voice.** |
| `playRotaryDetent` | 6 ms | 0.38 | The quietest stock transient. |

The filing sound sits at the **bottom of the app's register**: roughly
half the Throw's gain, no room/comb tail at all (the Throw's tail is
what makes a sound *fill a room*; this voice must die where it is
born), a fundamental an octave-ish under the seal body's 360 Hz, and
a total length about 1/25th of the Throw's. At arm's length, in a
quiet room, it is clearly audible to the man holding the phone. That
is its entire brief. It is not mixed to be impressive, present, or
memorable — if he notices the *sound design*, it has failed; he
should notice only that the tap landed.

### 2.5 Firing law — when it may and may not play

1. **On the commit, not on the tap.** The sound fires when the filing
   has actually been written — the sound must never claim *filed*
   for a write that failed. (Same truth discipline as the audit's
   print charges: no channel may assert what the store hasn't done.)
2. **Once per declaration, ever.** It fires on the undeclared →
   declared transition only. Recomposition, process-death restore,
   re-entering the Drawer, scrolling past yesterday's declared ILL
   line — all silent. A past filing is print; print does not re-speak
   on every viewing. Re-sounding a declared ILL at every visit would
   re-announce his sick day to the room indefinitely.
3. **The global sound fact governs it, wholly.** If sound is off in
   the app's setting, filing is silent and the retiring print line
   is the whole confirmation — the sound is a redundant channel,
   never the sole carrier of the fact. No Drawer-local sound toggle,
   no per-event volume control, no override of the system volume.
4. **Under seal:** filing is the one write this screen owns; whether
   an undeclared line is tappable under seal at all is the doctrine
   question the designer parked (designer §4 — never a painted door).
   This file adds only: *if* it files, it sounds as specified, seal
   or no seal — the sound is not a seal sound and must not change
   character under one. If it cannot file, it cannot sound.

---

## 3 · THE SIX REFUSALS — EACH ON ITS OWN MERITS

### 3.1 Opening the Drawer — NO SOUND

Entering a room to read is not an act the app should applaud or
announce. The tempting candidate — a wooden drawer slide — is the
worst sound in the brief after the stamp: the longest, the most
theatrical, and the one that fires on *every* entry, including the
sealed read-only entries the audit acquitted (§6) and the 01:00
errand, where a half-second whoosh announces a browsing session to
the room. It also lies physically: nothing slides; a screen replaces
a screen (the motion pass crossfades in place). Silence on entry is
also what makes the one filing sound legible — a room that whooshes
open has already spent its quiet.

### 3.2 Shelf row → MONTH — NO SOUND

A month row opening MONTH is navigation *deeper into the same
drawer*, not arrival anywhere new. A click here fails three ways:
it repeats (the designer's §3.6 use case is hunting across two or
three months in a row — click, click, click, a hunt that sounds
busy), it counts by ear (§1.1 — months opened become audible), and
it is unnecessary — the violet ~8% press wash and the view change
already confirm the tap in the channel the tap was made in. Nothing
was written; nothing is confirmed; nothing speaks.

### 3.3 Opening a task DETAIL — NO SOUND

DETAIL is the purest reading surface in the app — one slip taken out
and read, BACK its only verb (acquitted, §6). It is also, on the
1 AM errand, plausibly the most-opened view in the room. A page
rustle or paper lift here is cosplay: a pleasant fiction purchased
with a sound on every single read, at night, forever. And any
per-open sound sits one small step from per-*task* sound — the step
is refused at the top of the slope. The row's tap already answered
its own question ("how long" is on the row, SPENT acquitted); DETAIL
adds reading, not an event.

### 3.4 EARLIER paging — NO SOUND

EARLIER grows the list in place at his tap and retires at the
archive's end (acquitted as a mechanism, §6). Sounding it would be
the purest per-tap tally in the room — three taps to the bottom of a
long month would be three clicks: the archive's depth, counted
aloud. It would also need an end-of-archive sound to be consistent,
and an "nothing more" tone is either an error buzz (punishing him
for looking) or a soft stop (grading the archive as complete). Both
are refused. The content arriving under his thumb is the feedback;
the absence of a new EARLIER row at the end is the end, printed by
omission, silently — the same empty-silence the audit acquitted for
empty months.

### 3.5 Search typing / clearing — NO SOUND

- **Per keystroke:** nothing. Typewriter/key-click voicing is the
  highest cumulative noise available — a ten-letter query at 01:00
  is ten announcements — for information content of zero: his eyes
  are already on the field, and the characters print as he types.
- **On results / no results:** nothing, and this is the sharpest
  refusal in the file. A "found" ding is a reward for remembering
  the right word; a "not found" buzz is an error tone for failing a
  memory test the audit already named as search's weakness (§4).
  `NO STORED TASK UNDER "X"` prints honestly and must print in
  silence — an empty result is a fact about the archive, not a
  mistake by the man. Search in this room never scores the query.
- **On clearing the query** (by erasing, or by SEARCH's new BACK,
  designer §5): nothing. Clearing is retreating to the landing as he
  left it; an erase swoosh would make leaving search sound like
  discarding work. The query was never work. It was a question, and
  it is over, quietly.

### 3.6 BACK — NO SOUND, everywhere

One BACK costume visually (audit §2) — and one silence sonically,
for every parent: MONTH → LANDING, DETAIL → MONTH/LANDING,
SEARCH → LANDING (clearing the query), and each step of the
system-back walk consumed inside the Drawer before any global
ejection. Reasons, briefly: BACK is the most-used verb in the room
and the one most used at 01:00; a reverse-click or lowered latch
variant would frame retreating as undoing — as if looking at a month
and leaving it were a small failure to beep about; and a multi-step
back walk out of a deep browse must not become a descending cascade
of clicks in a quiet room, each step announcing the next. You put
the slip back and you leave. The drawer does not comment on your
leaving, ever.

---

## 4 · THE 1 AM WALK, SOUNDED END TO END

Muede's errand, with this spec applied (audit §4, designer §4):

1. *Open the Drawer to file tomorrow ILL before sleep.* — Silence.
   Nobody in the room learns his phone opened anything.
2. *Tap ILL on tomorrow's line — two taps total, then sleep.* —
   **One settle**: ~40 ms, low, half the seal's gain, no tail. He
   hears the tap land without opening his eyes wider or re-reading
   the line; the phone goes face-down. Honest limit, stated rather
   than designed around: any audible sound at full system volume in
   a silent room can be heard by someone beside him — the mitigations
   are its brevity, its low register, its absence of reverb tail,
   and the app's global sound-off being fully honoured (§2.5.3).
   What this spec will not do is pretend a daytime-louder version
   could exist.
3. *Next morning, re-opening the Drawer and seeing ILL printed beside
   the date in full ink.* — Silence. The filing speaks in print now,
   forever, and never again in sound (§2.5.2).

One sound in the whole walk, on the one act in it. That ratio is
the design.

---

## 5 · PRICE — WHAT THIS COSTS, WHAT THE REJECTED SETS COST

The engine synthesises everything; there are **no samples in this
spec and none are needed** — recording or sourcing real-world audio
would break the app's zero-audio-file charter (`TactileSoundEngine`
header) before it broke any budget.

| Item | Rough price | Verdict |
|---|---|---|
| **This spec as written** — one dedicated filing transient (`playFilingSettle`: one new function in the existing synth pattern, one call site fired on the filing commit) + a test pinning its gain/duration ceiling and its once-per-declaration firing | **~0.5 builder-day, all-in.** No sample work, no mixing pass, no asset work — the parameters *are* the mix. | **Recommended** |
| Cheaper floor — reuse `playSeatKnock` at its lightest weight verbatim, no new function | ~0.1 day (call site only) | Rejected on meaning, not price: the seat knock already speaks for wheel/gate landings elsewhere; a borrowed voice imports a borrowed meaning, and filing has no mass for its pitch law to express. The saving is ~0.4 day and buys a confusion. |
| A real paper-stamp or drawer-slide **sample** (source/record, edit, level across devices, bundle) | **~2–3 days** + permanent asset/charter cost | Rejected on meaning (§2.2) — the price is recorded so the rejection is not mistaken for a budget call. |
| The full per-event set — distinct custom voices for all seven events, mixed as a family | **~3–4 days** of synth + consistency work | Rejected whole (§1, §3). Priced so a future "just add small clicks" proposal carries its real cost and its real argument burden. |
| An ambient room tone / drawer-room bed on entry | Not priced | Not an option at any price: the engine charter forbids ambient loops, and a room that hums is a room that summons. |

**Beyond-stock total for the recommended spec: half a day, no
samples, no mixing days.** Everything else in this file is the
absence of work, deliberately.

---

## 6 · BUILDER HANDOVER — THE WHOLE ASK, IN FIVE LINES

1. Add one voice to `TactileSoundEngine` on the filing pattern of the
   existing transients: single low fundamental (~180–220 Hz),
   ~30–45 ms, gain ≤ 0.35, decay-only envelope, bright partial held
   back, **no room/comb tail**. Proposed name: `playFilingSettle`.
2. Fire it from the day-word filing path only, **after** the write
   commits, once per undeclared → declared transition (today and
   tomorrow lines, all three words, identical call, no parameters
   derived from the day, the word, or any stored content).
3. Add **no** sound call to: Drawer entry, month-row tap, task-row
   tap, EARLIER, search focus/typing/clear/results, BACK (header or
   system), or any rendering of an already-declared filing line.
4. The voice reads the engine's existing `soundEnabled` fact like
   every other voice — no new setting, no Drawer-local gate.
5. Test pins worth having: the voice's gain/duration ceiling, and
   that re-entering/rendering a declared day fires nothing.

## 7 · WHAT THIS FILE DELIBERATELY DOES NOT DO

- **No haptic spec.** The engine couples a waveform to most voices;
  what the filing *feels* like in the hand — and whether navigation
  gets any tactile tick — is the haptic seat's file, not this one.
  Nothing here forbids or requires a silent tactile twin for the
  filing; the audio half obeys this file either way.
- **No cabinet sound.** The cabinet is evicted from this room
  (audit §2A) and its home is parked in issue #21. Whatever it sounds
  like wherever it lands is that issue's question. The forge, in
  particular, gets no sound from this file, in any room, by this
  hand.
- **No seal opinion.** The Throw is bit-identical and untouched by
  everything above; this file's only act toward it is refusing to
  borrow it.
- **No day/night, sealed/unsealed, or per-look sound variants.** One
  room, one voice, one event — variants are how a quiet room learns
  to talk too much.

— Feel 2 — Sound (issue #8 cell)
