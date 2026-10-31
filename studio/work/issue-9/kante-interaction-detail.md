# KANTE — INTERACTION DETAIL — #9 PARK: THE CHECK VERB, PERFECTED
## Kante, interaction detail (dedicated-100 seat). The calmest screen in the app gets the slowest, deepest, quietest press in the app.
## Status: SPEC ONLY — no Kotlin edited, nothing compiled (Ren compiles, on Sarthak's word).

Grounding read before writing: AUDIT-park.md (Hof; P-1 CHECK cure),
ISSUE-9-WAVE2-REPORT.md (Vogt), and ParkDrawer.kt as built on agent/issue-9:
CHECK + FIX keys dip **1dp** via `animateDpAsState` — 90ms SEAT in
(`MonolithKin.PRESS_MS`), 160ms THROW back; `playSeatKnock` on press,
`playFirmClick` on the FIX return verdict; CHECK is DOWN-commit, generation-
gated CHECKING state, verdict line prints the first lack. Sheet rise 280 SEAT /
leave 160 THROW (KIN-DRAWER — acquitted, untouched below).

What is wrong at gallery standard: CHECK and FIX currently share ONE press
physics (1dp / 90 / 160 / same knock). They are opposite acts. FIX is an
errand — leave, repair, return. CHECK is a question the machine answers about
itself, asked in the calmest room in the app. Sharing a dip makes the calmest
verb in the app feel like every other key. This file separates them, and gives
CHECK a press with a beginning, a held middle, and an answered end.

---

## 1 · THE CHECK PRESS — three phases, one breath

- **Phase A — CONTACT (0–45ms):** travel 0dp → 1dp, `HubPressDip` curve
  `CubicBezier(0.20,0,0.30,1)`. The key meets the finger. Fill wash (the
  checkInk dress, wave-2) at 50% of its final strength by 45ms.
- **Phase B — COMPRESSION (45–110ms):** travel 1dp → **2.5dp**, easing
  `CubicBezier(0.2,0,0,1)` (house decelerate), wash to full. 2.5dp is the
  deepest travel anywhere in Monolith — reserved for this verb alone (FIX:
  1.5dp, §3; seal-family surfaces keep their own ruled physics and are out
  of this file's scope). The extra 1.5dp is travelled SLOWER than the first
  1dp on purpose: a calm press decelerates into its seat; a rushed one snaps.
- **Phase C — HELD ANSWER (110ms → sweep lands):** the key STAYS at 2.5dp for
  the whole CHECKING state (replaces the current behaviour where checkDip is
  a momentary dip). A key that pops back up while the machine is still
  answering lies about the act being finished. Release of the finger does
  NOT raise the key; only the sweep's landing (the existing generation gate,
  `lastCompletedSweepGen >= checkPressGen`) releases it:
  **return 2.5dp → 0dp over 220ms**, `HubSeatReturn`
  `CubicBezier(0.22,0.90,0.28,1)` — slower than FIX's 160ms return, because
  an answered question settles; it is not thrown back.
- **Timeout truth:** if no completing sweep lands within 4,000ms of commit,
  the key returns over 220ms anyway and the verdict line prints the existing
  lack form. A key held down forever by a dead sweep is a stuck machine, and
  PARK never prints a stuck machine as calm.
- **Re-press while CHECKING:** ignored entirely — no re-dip, no re-knock, no
  queue (the code already refuses a second generation; this extends the
  refusal to the FEEL: the finger finds a key already down, travel stays
  2.5dp, nothing fires). 
- **Reduced motion (animator scale 0):** travel collapses to 0dp in all
  phases; the CHECKING word + verdict line carry the state alone. No
  information lives only in the dip.

## 2 · CHECK — HAPTIC + SOUND + LIGHT (the quietest signature in the app)

- **Haptic, on commit (DOWN):** one waveform, timings `[0, 12, 40, 16]` ms,
  amplitudes `[0, 80, 0, 50]` — a soft strike, a 40ms breath, a smaller
  settle. Total 68ms, peak amplitude 80/255: the lowest peak of any commit
  haptic in the app. It must be feelable in a held phone and unfelt on a
  table (no buzz-through). Pre-API-26 fallback: single 12ms one-shot at
  default amplitude — never the double pattern faked with two calls.
- **Haptic, on answer:** NONE when the verdict is whole (GUARD n/4 complete —
  silence is the good news; a buzz for "all fine" trains him to need one).
  When the verdict names a lack: ONE 9ms pulse at amplitude 60, fired at the
  same frame the verdict line prints — the lack is already printed; the pulse
  only says "look down", once.
- **Sound, on commit:** `playSeatKnock` stays but re-voiced for CHECK only:
  gain 0.35× the FIX knock's gain, low-passed at 900Hz (the existing engine
  is procedural PCM — a one-pole low-pass at 900Hz on the knock buffer is
  the implementation note). A knock through a closed door, not on it.
  **Sound, on answer:** none, whole or lacking. PARK makes exactly one sound
  per CHECK act, at its start; the answer is print.
- **Light:** no glow, no shadow change, no scale. The ONLY light event is the
  wash of §1. The key's shadow is constant at rest and pressed (a shadow that
  shrinks on press is button-cliché #1; the travel + wash already say it).
- All of the above respects the existing sound-effects setting: sound OFF =
  haptic only, at the same timings; haptics are never gated by the sound
  toggle (different channel, different law — the 01:00 case is served by the
  amplitudes, not by a settings hunt).

## 3 · FIX — deliberately NOT upgraded to match

FIX keeps a distinct, lesser press so the thumb can tell errand from question
eyes-shut: travel **1.5dp**, 70ms in (seat curve), **160ms return** (as
built), `playSeatKnock` at full gain unfiltered, `playFirmClick` on the
return verdict (as built — acquitted). FIX haptic: single `[0,10]` ms at
amplitude 120 — firmer and shorter than CHECK's, because an errand is a
click, not a breath. If a builder is tempted to unify the two keys'
physics for code neatness: that unification is the defect this file exists
to prevent. Two verbs, two hands.

## 4 · THE SHEET AROUND THE VERB — two deltas only

- **Grip strip press** (as built: width 24dp→28dp while gripped): keep the
  width change, add the timing it lacks — 120ms in, 180ms out, house
  decelerate. An instant 4dp jump reads as a glitch; 120/180 reads as the
  strip taking his grip. No haptic on grip (he is moving furniture, not
  committing an act); the existing drag-past-threshold haptic, if any lane
  adds one, is out of scope and must not be added here.
- **Board rows at rest:** 0 animation, 0 haptic, 0 sound, forever. The age
  column's word changing (NOW → 45S AGO) is a print change, applied as a
  cut — never a fade, never a tick. A board that fades its ages is breathing,
  and PARK does not breathe.

## 5 · ACCEPTANCE (phone-checkable)
1. CHECK press visibly travels further and slower than FIX; key stays down until the verdict prints, then returns over ~220ms.
2. CHECK knock is audibly softer/darker than FIX knock at same volume; no sound at answer.
3. Whole verdict = no answer haptic. Lack verdict = exactly one 9ms pulse, with the print.
4. Second press during CHECKING: nothing moves, nothing sounds, nothing queues.
5. Animator scale 0: no travel anywhere; CHECK still fully usable by print alone.

— Kante, interaction detail
