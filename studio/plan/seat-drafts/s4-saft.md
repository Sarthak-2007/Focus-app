# §4 TASKS — FEEL INVENTORY (seat draft, Saft, for Sana to merge)

*Drafted by Saft (Designers, Game-Feel — the feel ledger) to merge into
MASTER-PLAN §4 (TASKS). This draft governs FEEL ONLY: every haptic,
every sound, and every motion the Tasks screen may make, each tied to
its event and its owner. Geometry, type and tone are Druck's / Set's
(Set's §4 type draft is the companion file); this draft takes their
surfaces as given and says what those surfaces do in the hand and the
ear.*

*Authorities, in the order this draft obeyed them: MASTER-PLAN §0
(the three standards, his verdicts) · plan/REGISTER.md (one value per
constant — where the register speaks, it governs and every rival
value below is named superseded, never forked) · DESIGNERS-PLAN §5
(the merged Tasks floor) + §1.5 (Kin's motion grammar) + §1.6 (Ton's
SCORE, the normative sound/haptic file) · IMPROVERS-PLAN ladders
12–14 as ruled by that plan's Arbitration (esp. Arb. 1 and 5) ·
Linea's Counterfoil §2.4 for the Deck-side DONE boundary (§4.F6).*

**Sealed-state law (this section's feel, per §0.1-5):** while a
session is sealed, Tasks offers no act at all — verbs and composer
are ABSENT, not greyed (DESIGNERS-PLAN §5); rows are read-only
(Zeile Rung 4). Therefore this screen makes **no sound and no haptic
while sealed**, without exception: there is no gesture left to own
one. The only motion is print changing when its fact changes — the
running row's remaining-time line steps on the minute tick (measured
number, hard cut, 0ms animation). Session sounds (finish, break,
catch) live in MASTER-PLAN §5, never here.

**Global feel laws carried in (not re-argued):** contact answer
≤16ms on every DOWN · one mover per input · overshoot ≤3% of travel
or 2dp, whichever binds · scale floor 0.94 · haptic/sound onsets
within ±8ms of each other, visual pair in the same 100ms window
(Ton sync law) · night = sound ×0.6, haptic ×0.85, geometry and
durations identical day/night (Ton §5 / Kin §1.5) · pitch is a fixed
set member, never computed from a value (Ton §0.3, DESIGNERS R15) ·
silence is the default: any Tasks event not in §4.F2 is silent and
still, by law, not by omission.

---

## 4.F1 Freeze verification — the spectacle budget on this screen

REGISTER (Spectacle) and MASTER-PLAN §2.6 (S2, closed) fix exactly
four freezes app-wide: **slide commit 40ms · final sub-step 50ms ·
DONE landing 40ms · bank catch 40ms.** A fifth requires naming which
of the four dies. Verified against this screen:

| Tasks freeze | Budget line | Scope | Verified |
|---|---|---|---|
| Final sub-step hit-stop | #2 — 50ms | Tasks surface only | ✔ spends exactly the registered 50ms, at the final-step tap (Saft plan §3b; DESIGNERS §5) |
| DONE landing hit-stop | #3 — 40ms | The falling row only — the rest of the screen stays live | ✔ spends exactly the registered 40ms, at DONE (Saft plan §3c) |

**No other freeze exists on this screen.** Every other candidate
found in the sources is convicted in §4.F7 and spends 0ms here:
ordinary sub-steps (Zeile's 40ms mid-strike pause, Luz's 60ms,
Druck's/Luz's 110ms final-step freeze, Luz's 80ms DONE hold). Zeile's
120ms DONE receipt hold (§4.F5, flag U1) is a *dwell* — the struck row
held as a receipt while the screen stays live, in the family of the
seal's ≥120ms dwell (DESIGNERS R7: freeze and dwell are two moments,
not one) — it is not a hit-stop and does not enter this ledger; if
the merge reclassifies it as a freeze, it becomes a fifth and one of
the four must be named to die. This draft does not name one.

---

## 4.F2 The score — every haptic and sound Tasks may make

Owner codes (Ton G1, one gesture / one owner / one waveform):
**[V]** the view owns it (contact feels) · **[VM]** the ViewModel
owns it (outcome feels). The other layer is silent for that gesture
by construction. Amplitudes are day values; night scaling above
applies to every row.

### Composer — caps, focus, submit (Fach 12 · Taste 13 · Ton rows 14–15)

| # | Event | Sound | Haptic | Owner |
|---|---|---|---|---|
| F-1 | Duration cap select (15/25/45/90) | **Wooden Bar** — CAP SET member for that cap (Ton §1.2: 15→C4 261.63Hz · 25→A3 220.00 · 45→F3 174.61 · 90→D3 146.83), bar voice 90–140ms decay · **−11dB** | `[0,8,6] @ [0,150,55]` · 14ms | [V] |
| F-2 | Re-dip of the already-selected cap | Same CAP member, felt-damped (decay ×0.5) · **−15dB** — "already down" is audibly different from "going down" | `[0,5] @ [0,90]` · 5ms | [V] |
| F-3 | Composer field gains focus | **Paper-slide** — shaped noise, highpass sweep 900→2,400Hz, 140ms · **−22dB** | `[0,4] @ [0,70]` · 4ms | [V] |
| F-4 | Field blurs with an un-added draft kept | Paper-slide reversed · **−26dB** (the draft is *put away*, never "discarded") | none | [V] |
| F-5 | **Submit — ADD tap or IME Done, non-empty (ONE event, both paths)** | **Card Punch** (Ton row 15): stage 1 punch at receipt — sine drop 320→140Hz, 30ms + noise lowpass 700Hz · **−4dB**; stage 2 slice at +90ms — noise bandpass sweep 3,000→1,200Hz, 70ms · **−12dB**; the new row's seating at its landing frame sounds **its own duration's CAP SET pitch · −8dB**, the two displaced neighbours knock once each · **−20dB** | Punch `[0,14,30,8] @ [0,230,0,160]` · 52ms; seating `[0,7] @ [0,185]` · 7ms at the landing frame | [V] — fired **on the FiledReceipt, after the commit** (Alba, §4.F7-C2), never on finger-up |
| F-6 | Empty submit (ADD or IME Done, field empty) | **One dull paper tap** · **−20dB** — a contact receipt, not an error; no punch, no buzz, no detent | `[0,5] @ [0,80]` · 5ms | [V] |
| F-7 | Typing in the field | **Silence** — the system keyboard owns its clicks; doubling them lies about which object is sounding (Ton §5) | none | — |

### Sub-steps — inside the ACTIVE block (Zeile 14 · Ton rows 16–17)

| # | Event | Sound | Haptic | Owner |
|---|---|---|---|---|
| F-8 | Ordinary sub-step check | **Step Note** — STEP SET member for that ordinal (Ton §1.3: 01→E4 329.63 · 02→G4 392.00 · 03→B4 493.88 · 04→D5 587.33 · 05→E5 659.25Hz; 06+ reuse the E5 voice, decay ×0.6 — no sixth pitch is ever invented), bar voice ~110ms · **−13dB** | `[0,5] @ [0,120]` · 5ms (Step-05 variant `[0,5] @ [0,152]`) | [V] |
| F-9 | Sub-step un-check (correction) | Same STEP member, felt-damped (decay ×0.5) · **−17dB** — the same note taken back, never an error note | `[0,4] @ [0,80]` · 4ms | [V] |
| F-10 | **Final sub-step check** (the check that completes the set) | **The Strum** — all of *that task's own* STEP SET pitches, only the steps it has, staggered 12ms apart low→high, summed peak **−1dB**, decay ~480ms, through the app's one small room tail; then at **+160ms** the **Paper Stamp** (MASS/Paper, 110Hz) · **−6dB**. Never the seal, never the accession stamp | One pulse per strum note (e.g. 3-step: `[0,6,12,6,12,8] @ [0,170,0,190,0,215]`); stamp `[0,22] @ [0,235]` + settle `[0,8] @ [0,90]` at +50ms after the stamp | [V] |

### DONE, START, CUT, promotion (Zeile 14 · Ton row 18)

| # | Event | Sound | Haptic | Owner |
|---|---|---|---|---|
| F-11 | **Task DONE — the stamp** (tap on DONE in the ACTIVE block) | **Paper Stamp, alone** — MASS/Paper (110Hz dry press + filtered-noise press) · **−6dB**, ~70ms. If steps are still open: **one dull paper fold instead** (noise lowpass 900Hz, 60ms · **−16dB**) — closed-incomplete sounds incomplete. The Final-Step Strum never re-fires here (row F-10's stamp and this stamp are two acts' stamps; the *strum* is counted once) | `[0,22] @ [0,235]` + settle `[0,8] @ [0,90]` at +50ms · ~80ms | **[VM]** — DONE is an outcome, not a contact |
| F-12 | **Task DONE — the landing** (the falling row seats below the foundation) | **No sound.** The stamp already said *done*; a landing clack would count the completion twice in sound | **One low pulse, duration tiered by the task's minutes (DESIGNERS §5 / Druck §3.4): ≤25M → 14ms · 45M → 18ms · ≥90M → 24ms**, one-shot, at the landing frame (Ton row 18's drop pulse `[0,18] @ [0,200]` is the middle tier's voicing; per-tier amplitudes are flag U3) | [VM] |
| F-13 | START (the screen's one orange key) | **None — deliberately.** START hands the task to the Deck; the arm/seal owns its sound there. A sound here would be a trailer for another sound (Druck §3.4; Zeile peak) | One-shot **10ms @ 200** on fire — the strongest feel in the rows element | [V] |
| F-14 | CUT (delete the ACTIVE task) | **Silence.** Deletion is clerical; it gets no ceremony (Druck §3.4) and undo-delete is on Ton's demoted-silent list | none | — |
| F-15 | Promotion (tap a queue row → it becomes ACTIVE) | **Mechanical latch** — Zeile Rung 3's filed sound, flagged U4 (not in Ton's SCORE; Sana/Ton to adopt or strike) | Waveform `[0,10,50,4]`ms @ `[0,160,0,90]` — a latch and a settle | [V] |
| F-16 | Queue-row press (DOWN only, incl. a press that does not promote) | Silence | none — the press is carried visually only (§4.F3-M9) | — |
| F-17 | ADD STEP line tap → step field opens; step added on return | Silence, except F-3/F-4 if the field's focus changes voice it — no step-add sound exists in any source; none is invented here | none | — |

**Everything else on this screen is silent, by name:** scrolling
the queue · the Footing changing (F-18, below) · done rows (inert —
no tap, no verb, no feel) · bank entrance/exit (§4.F3-M2 — the bank
appearing is the field's event, not the bank's act; Taste Rung 4) ·
screen arrival/departure (chrome's Panel Travel Law, MASTER §6).

| F-18 | Footing / queue-total change (any cause) | **None, defended finally** (Horiz Rung 2): every change was caused by a control that already fired its feel — a second feel here double-counts the act | none | — |

---

## 4.F3 The motion inventory — every motion Tasks may make

Curves/springs are Kin's registry ONLY (DESIGNERS §1.5): THROW
(0.20,0,0,1.00) — Kin's rename of the old CurveA, same bezier ·
SEAT (0.22,0.90,0.28,1.00) · PRESS (0.20,0,0.30,1.00) ·
LINEAR-MEASURED · springs SNAP-WHEEL 280·0.78, THROW-SPRING
520·0.90, RETURN-CRITICAL 300·1.00, PRESS-BACK 800·0.65,
BANK-RETURN 110·0.85. The old CurveB/C names in the Improvers seat
files are read through §4.F7-C8. One mover per row.

| # | Event | The one mover | Curve / spring | Duration | Notes / never moves |
|---|---|---|---|---|---|
| M-1 | Composer focus deepen | Field recess overlay + catch-line | 180ms, THROW (Fach Rung 1/3) | 180ms | Field deepens one tone step (Ton F-3's visual pair); horizontal geometry never moves |
| M-2 | Bank slot opens / closes (focused only) | The bank slot (height + alpha) | durations reconciled across Fach Rung 2 + Taste Rung 4 (curve name = flag U5) | open: height 260ms, content alpha 120ms · close: alpha 100ms, height/travel 180ms | Hit targets live at final geometry from t=0 (Taste Rung 4) — the entrance never taxes an entry. Caps' catch-light pass (Taste Rung 4) is listed here as filed and flagged U5 |
| M-3 | Key dip — caps, ADD, START (Kin M-12, one press vocabulary) | The key only: translate +1.5dp toward panel, shadow collapses, no scale | down PRESS 80ms (house value; legal range 70–90 — Taste's 70ms sits inside it); back PRESS-BACK, settled ≤150ms | dip 80ms · return ≤150ms | The key's action commits as the returning key crosses 99% of return (≈60–80ms after release). START's skirt travel (3dp→1dp, DESIGNERS §5) is this dip rendered in its skirt, not a second vocabulary. Already-selected cap: dips and returns, does nothing else (state) |
| M-4 | Submit swallow | Field contents (text + echo) | THROW | translate +5dp Y and fade to 0 over 110ms, **starting on the receipt** (re-timed per §4.F7-C2) | The well ingests downward, toward the queue. Focus, bank, keyboard and minutes never move |
| M-5 | New row enters the queue | The new row only | 260ms (curve per U5) | fade 0→1 + rise from +10dp, 260ms, starting as the well finishes emptying | Seating feel F-5 fires at this row's landing frame, not at animation start |
| M-6 | The QUEUED stamp | Print only (placeholder slot) | — | `QUEUED · {minutes}M · #{position}` — **values from the FiledReceipt, never from a t=0 prediction** (Alba) — holds 420ms, fades 260ms THROW (680ms total); typing cancels it instantly | A computed print change, not a mover (Kin M-01 law) |
| M-7 | Footing re-strike | Print only | — | Glyph swap instant + 120ms letterpress ink-settle (88→100%) | Computed numbers never roll, flip or count through intermediates (Horiz Rung 2; DESIGNERS §5). On DONE it re-strikes **on the landing frame and no earlier** (§4.F5) |
| M-8 | Ordinary sub-step strike | The step row | wipe cubic-bezier(0.7,0,0.2,1) (Saft §3a) | strike tears L→R **110ms** (flag U2 — rival durations in sources), 1.5dp, nib-headed, tapering tail; 80% of width in first 50ms | Squash: row scaleY 1.0→0.96 in 45ms anchored at baseline, spring back ζ0.55/380, one overshoot to 1.015, settled ≤170ms. Settle: struck row drops one tone step; numeral → tick; next unstruck step nudges up 3dp at +35ms over 110ms. Rapid repeats: second tap buffered to the first's settle frame; third coalesces to a 90ms plain strike. Un-strike: wipe reversed 90ms |
| M-9 | Queue-row press / promotion travel | Press: the row's ink only. Promotion: the promoted row + the collapsing former ACTIVE | THROW | press: ink steps one tone up for the press's duration, ≤16ms answer, no fill/ripple/scale (Druck §4; Kin N-01). Promotion: height animation 280ms on the step grid; `animateItem` placement 420ms — the row visibly travels to the top instead of teleporting; marker fills crossfade over the same 420ms (Zeile Rung 3) | Neighbouring rows are not second movers — placement is the list's one motion |
| M-10 | **Final sub-step strike** | The step row, then the title block | wipe as M-8, heavier | **Freeze 50ms at the tap (ledger #2, §4.F1)** · strike wipe **170ms at 1.9dp** · squash to the 0.94 floor, same spring, settled ≤230ms · then the bracket: the parent title's hairline closes L→R over **150ms** (0.2,0.9,0.25,1) and the title block recesses one tone step | **The parent title is NOT struck** — bracket = steps finished, strike = task finished (§4.F6). Fossils on the task's rows brighten one tone step in strum order, 12ms stagger (Ton row 17 visual pair) |
| M-11 | **DONE — the fall** | The falling row only | **ease-in only** cubic-bezier(0.5,0,0.9,0.4) — gravity; an ease-out fall is a build error, not a tuning choice (Saft §3c) | **By the task's minutes (§5/Saft §3c): 15M → 260ms · 25M → 320ms · 45M → 400ms · 90M → 480ms** (band assignment for off-cap minutes = flag U3) | Sequence: DOWN ack ≤16ms (DONE word sinks one tone) · **freeze 40ms, row-local (ledger #3)** · title strike **140ms** concurrent with the first 4dp of fall — the row dies falling, not before it · fall · landing frame: compression of the done stack **1dp (15M) / 2dp (25M) / 2dp + row scaleY dip to 0.97 (45M) / 3dp (90M)**; 90M band only: a second 1dp compression at landing +90ms — heavy things land twice. No rotation, ever |
| M-12 | Close-up after DONE | The rows below the landed row | spring ζ0.62 / stiffness 300 | rise starting at the landing frame, ~320ms | The queue closes because the weight left. If the dropped task was ACTIVE: the next task does **not** auto-promote during the drop — promotion is its own event at close-up settle +120ms (M-9 then runs as its own act) |
| M-13 | CUT collapse | The slab/row | THROW | height collapse 280ms; queue rises in the same window | No drop, no strike, no landing — clerical (Druck §3.4) |
| M-14 | Empty-submit placeholder re-strike | Print only | SEAT (Kin's CurveC replacement, §4.F7-C8) | alpha 1→0.45→1, 240ms | No shake, no toast, no scolding text (Fach Rung 1) |
| M-15 | Running line under HOLD | Print only | — | remaining-time text steps on the minute tick, 0ms animation | Measured number: hard cut (Kin M-17 law applied to this screen's one live print) |

**Never on this screen (Kin §4, applied):** list entrance staggers
or cascades · rolling/flipping numerals · ripple · celebration of
any kind · idle/ambient motion — idle Tasks passes the 5s
screenshot-diff stillness test, same as the Deck · a second mover
inside any row above.

---

## 4.F4 (reserved — geometry/type land in Druck's and Set's drafts)

---

## 4.F5 The DONE sequence, assembled once

So the merge never has to reassemble it from five files. ACTIVE
task, steps already complete (the strum, F-10, has sounded at the
final check — an earlier, separate act):

1. **DOWN (≤16ms):** DONE word sinks one tone step. Nothing else.
2. **Freeze 40ms** — row-local, ledger #3. The row stops; the
   screen doesn't.
3. **Stamp (F-11, [VM])** fires with the strike's onset.
4. **Strike + fall (M-11):** title strike 140ms concurrent with the
   first 4dp of fall; fall 260/320/400/480ms by minutes, ease-in
   only.
5. **Landing frame:** compression per M-11 · landing pulse F-12 ·
   **the Footing re-strikes on this frame and no other** — between
   tap and landing the screen holds the task as *falling*, which is
   the truth of it (Saft §3c; Improvers Arb. 14: never on DONE
   press, never mid-strike).
6. **Close-up (M-12),** then promotion as a separate event at
   settle +120ms if a next task exists.

Zeile Rung 4's **120ms receipt hold** (struck row held before the
drop, undo cancelling during strike + hold) is **not** placed in
this sequence — it cannot coexist with step 4's strike-concurrent-
with-fall as filed by §5, Druck and Saft §3c. It is carried as flag
**U1**, unplaced, for Sana to rule. The undo window it carries is
likewise unplaced until U1 is ruled.

---

## 4.F6 The DONE boundary — Deck vs Tasks (one fact, two homes, one owner each)

The same underlying act — *this task is complete* — can be
performed on two screens. It must never sound twice, and each
screen's voice belongs to that screen alone:

| | Deck — the Counterfoil tear (Linea §2.4) | Tasks — the ACTIVE block (this section) |
|---|---|---|
| Gesture | Tap the tear label, or drag the leaf ≥24dp and release | Tap the DONE word in the slab |
| Sound | **`playCompletionPulse()` and nothing else** — bundled waveform `[0,10,24,10,24,14]`ms @ `[0,175,0,125,0,80]` + sound (185Hz, 64ms, gain 0.68, chord 1.33), with at most one paper-tear noise layer (band-passed noise 900–3,200Hz, 90ms, gain 0.22) layered **under the same call site** — if the engine cannot layer, the tear ships with the pulse alone, never a second call | **Paper Stamp, alone** (F-11) + silent landing pulse (F-12). The completion pulse NEVER fires on Tasks |
| Owner | The Deck task-line element, single call site (Linea §2.4: "one owner, one stack") | [VM] outcome owner (Ton row 18) |
| Visual | Leaf detaches at the perforation: down 10dp, fade 0 over 180ms THROW; next leaf revealed already printed, striking to full over 260ms | Strike + fall + landing (M-11) |

**Race law (Rurik's double-DONE race, invention/maximpl-parts/07
§Inv.2, applied as feel law):** Deck DONE and Tasks DONE are wired
to the same completion path in shipped code. Completion is **one
event**: whichever surface's act commits it first owns the voice
above; the other surface renders the result **silently** (state
change, no replay, no second sound on navigation or re-entry). A
second DONE act on an already-completed task is a null act —
silent, no haptic, no write (the Yuki pattern, §4.F7-C1).

**Sub-step boundary, same discipline:** on the Deck foil, step
toggles are **haptic-only, silent** (4ms one-shot; expand 4ms /
collapse 2ms — Linea §2.5–2.6). On Tasks, step checks carry the
Step Note (F-8). Same steps, two surfaces, never simultaneous —
the Deck is the confirmation surface and stays quiet; Tasks is
the working surface and speaks. Neither surface imports the
other's voicing.

**What DONE never borrows, on either screen:** the seal (rationed
to arm / break / routine firing — Ton §0.4) · the accession stamp
(session finish and day close only — a finished *task* is not a
finished *session*).

---

## 4.F7 Convictions — double-fires and pre-commit sounds found in the sources

The pattern is Yuki's (invention/maximpl-parts/04; merged in
MAXIMPL-INVENTIONS): a sound fired before the transition it claims
to report, or two sounds for one transition, is a lie in the hand —
however good each sound is alone. Applied to every Tasks source:

- **C1 — The composer submit double-fire (LIVE in shipped code,
  Alba's evidence, invention/maximpl-parts/06 §2).** One filing
  today produces **two** commit sounds, both *before* the database
  write: the screen's `playMechanicalLatch(true)` at
  `FocusSequencerScreen.kt:121` (inside `submitTask()`, before
  `onAddTask`) and the ViewModel's `playRotaryDetent(680.0)` at
  `MonolithViewModel.kt:527`. **Verdict: both die.** The submit
  sound is F-5's Card Punch, fired once, by the view, **on the
  FiledReceipt** — after the transaction that filed the task
  (Alba Inv. 3: `fileDraft()` returns the receipt; a blank draft
  returns null and sounds nothing but F-6). Same cure as Yuki's
  sled: refusal/blank mints no feel; the committed fact renders
  its own sound. The Deck quick-add / checklist callers of the
  legacy `addTaskToQueue` keep that path until they adopt the
  receipt (Alba's stated scope) — the conviction binds the
  composer path this section owns.
- **C2 — Fach Rung 4's t=0 latch timing.** As filed, the latch
  fires at t=0 and `onAddTask` at t=60ms — the sound precedes the
  filing it announces, and the rung's stamp prints minutes/position
  **captured at t=0** (a prediction the repository is free to
  contradict: predicted `QUEUED · #04`, landable as ACTIVE #01).
  **Verdict:** sound and stamp both re-time to the receipt (F-5,
  M-4, M-6); the swallow motion starts on the receipt, not on the
  finger. The latch itself is separately retired by C3.
- **C3 — Three submit voices for one event.** Fach: mechanical
  latch (760Hz/16ms/gain 0.54; haptic `[0,11@215, 26 gap, 9@150]`).
  Taste Rung 2: card punch (210Hz/30ms/gain 0.58/chord 0.62; haptic
  `[0,10@230, 18 gap, 6@140]`). Ton row 15: card punch (320→140Hz
  stage 1 + slice stage 2 + CAP seating). **Verdict (reconciled,
  §4.F8-R1):** IMPROVERS Arbitration 1 already ruled the *identity*
  — the card punch is THE submit sound on both paths, the latch
  retires from submit; Ton, the normative score (§1.6), governs
  the *parameters* — F-5 is Ton's voicing whole. Taste's punch
  parameters and Fach's latch are superseded, not forked.
- **C4 — Zeile Rung 4's DONE latch.** `playMechanicalLatch` at drop
  start, on top of the stamp: wrong voice (the latch is a filing/
  promotion voice; DONE's voice is the Paper Stamp), wrong moment
  (drop *start* — it announces a landing that has not happened),
  and a second heavy feel stacked on F-11. **Verdict: dies.**
  DONE's feels are F-11 + F-12 and nothing else.
- **C5 — Ordinary sub-steps spending freezes.** Zeile Rung 2 files
  a 40ms hit-stop pause at ~70% of *every* step strike; Luz files
  60ms per strike (halved on repeats — still a spend ~30×/day).
  **Verdict: both die.** Ordinary strikes spend **0ms** of freeze
  (Saft plan §3a verdict, DESIGNERS §1.5: "ordinary sub-steps spend
  zero"). A freeze spent thirty times a day reads as lag by day
  two; and Zeile's 40ms is ledger #3's number — DONE's — spent on
  the wrong event.
- **C6 — The 110ms final-step freeze (Druck §3.3; Luz, plan-wave
  and her Commons correction).** Both file the final sub-step at
  **110ms** freeze + a third haptic pulse (20ms @ full). REGISTER
  fixes this freeze at **50ms**. **Verdict:** 110ms dies wherever
  it appears; the third pulse dies with it (Ton's strum + stamp,
  F-10, is the final step's whole haptic — a third pulse stacked
  on the stamp waveform is a double-fire in one gesture). What
  survives of Luz/Druck, whole and credited: the *withholding* —
  everything escalates except the title strike (M-10).
- **C7 — Luz's 80ms DONE hold.** Filed as a row-local hit-stop at
  DONE (wild-seats/05). REGISTER fixes DONE's freeze at **40ms**,
  row-local (Saft §3c). **Verdict:** 80ms dies; 40ms governs.
- **C8 — Pitch-table forks for the caps.** One cap, three tables:
  Ton CAP SET (146.83–261.63Hz, §1.2) · Taste Rung 2 bars
  (494–784Hz) · Alba's Mint enum (587.33–1,046.50Hz). REGISTER's
  rule — one value per constant — and §1.6's normativity give one
  answer: **Ton's CAP SET governs (F-1/F-2/F-5 seating).** Taste's
  and Alba's tables die *as sound sources*; Alba's `CapDuration`
  enum survives carrying minutes only, its `pitchHz` field struck
  (the cap a user taps and the pitch that seats must read one
  table — Alba's own argument, turned on her table). Note the
  STEP SET has no fork: every source that pitches steps uses
  Ton's set or stays silent.
- **C9 — Zeile's landing-haptic formula.** Rung 4 computes the
  landing pulse from minutes: `D = clamp(6 + minutes/5, 8, 24)`ms.
  A feel computed from a value by formula is the haptic twin of
  the banned pitch formulas (Ton §0.3; Saft's mass law: identity,
  not arithmetic). **Verdict:** the formula dies; the fixed tier
  lookup governs (F-12: 14/18/24ms by band, from §5/Druck).
  Zeile's *principle* — a 90-minute task lands measurably heavier
  than a 15-minute one — survives whole; only its arithmetic dies.
- **C10 — Fach's empty-submit detent.** `playRotaryDetent(320.0)`
  + waveform `[0,7@180, 5@70]`: 320Hz is the dead sled-map pitch
  (MASTER §3.7: "320Hz dies"), in no fixed set, and a *detent*
  claims a value ticked — nothing ticked. Taste's idle thud
  (6ms @ 45) is a third voicing of the same act. **Verdict:** both
  die; Ton's silence-list entry governs both empty paths (F-6:
  one dull paper tap −20dB, `[0,5] @ [0,80]`). Fach's placeholder
  re-strike (M-14) survives — it is motion, not sound.
- **C11 — Settle/arrival double-announcements in the ladders.**
  Zeile Rung 1–2 haptics for step toggles (4ms @ 90 silent;
  8ms @ 140 on strike-complete) and Druck §3.3's (8ms; un-tap 4ms)
  are rival voicings of F-8/F-9 stacked on Ton's row 16. **Verdict:**
  Ton row 16 governs whole (F-8/F-9); the ladders' numbers are
  superseded. A step check fires exactly once, at the strike's
  onset frame, from [V].

*No conviction is made against the shipped Deck tear (Linea
§2.4): its stack is single-owner, single call site, fired by the
act it reports — it is the standard the convictions above were
measured against, and §4.F6 keeps it intact on its own screen.*

---

## 4.F8 Reconciled conflicts — settled by authority, not by invention

- **R1 — Submit sound identity + form of ADD.** IMPROVERS Arb. 1:
  ADD is **text** in Fach's sticky line in both states; Taste's
  card punch is the submit sound on both paths (IME Done and ADD
  tap can never diverge — one `onComposerCommit()`); Fach's echo
  lives collapsed-only. This draft's F-5/M-3/M-4 are written to
  that ruling. Taste's objection (a text word carrying travel
  would lie about its form) is answered by Arb. 1 itself: the
  travel is dropped, not the word — ADD text dips per M-3's key
  law as a *word-key*, and the punch is the receipt's sound, not
  the word's.
- **R2 — Cap press mechanics.** Taste Rung 2 (press-in 70ms,
  release 180ms EasingB + MechanicalOvershoot) vs Kin M-12 (PRESS
  80ms house / 70–90 legal; PRESS-BACK settled ≤150ms;
  MechanicalOvershoot *deleted*, §1.3). Kin is the motion-grammar
  owner: M-3 governs. Taste's 70ms press-in survives inside Kin's
  legal range; his 180ms release and the deleted overshoot do not.
- **R3 — Focus voicing.** Fach files focus/typing as no-sound
  ("focus is navigation"); Ton's score explicitly completes the
  Tasks desk with the paper-slide (F-3/F-4). Ton is normative for
  sound (§1.6): the paper-slide governs. Fach's objection is on
  record here; his focus *state* (the boolean every rung keys off)
  is untouched by the ruling.
- **R4 — Re-dip of a selected cap.** Taste Rung 2: total silence,
  no motion. Kin M-12: the key dips and returns and does nothing
  else. Ton row 14: a damped re-dip sound. Read together: Kin's
  "nothing else" governs *state/action* (no re-fire, no change);
  Ton governs *sound*. M-3's dip + F-2's damped bar is the merged
  act. Taste's absolute silence is superseded by the normative
  score.
- **R5 — Final-step strike width.** Saft §3b: 1.9dp. Druck §3.3:
  2dp. M-10 files **1.9dp** (the feel ledger owns this event's
  spec; Druck's 2dp reads as its rounding — the difference is
  sub-pixel at build). Named here so the merge can reverse it in
  one line if Druck's seat objects.
- **R6 — Stamp at DONE vs stamp at final step.** Both fire the
  Paper Stamp (F-10 at +160ms after the strum; F-11 at DONE).
  These are two acts and two facts (steps-complete ≠ task-
  complete), each stamped once — the same discipline as §4.F6's
  boundary. The strum, not the stamp, is the counted-once element
  at DONE (Ton row 18, verbatim in F-11).

---

## 4.F9 Unresolvable from the sources — flagged, not settled

*Sana: these are yours (or the named owner's). This draft invents
no resolution for any of them; the inventory above is written so
each can be ruled without re-drafting.*

- **U1 — Zeile's 120ms receipt hold vs strike-concurrent-with-fall.**
  Zeile Rung 4 (strike 180ms → 120ms hold → collapse/drop, undo
  live during strike + hold) cannot coexist with §5/Druck/Saft
  §3c (strike concurrent with the first 4dp of fall; no hold).
  One of the two DONE shapes must die, and the undo window's fate
  rides on the choice. *Owner of the call: Sana/Ren.*
- **U2 — Ordinary strike duration: 110 vs 140 vs 180ms.** Saft
  §3a: 110ms. Ton row 16 visual pair: 140ms. Druck §3.3 / Zeile
  Rung 2 / DESIGNERS §5's "strike in 180ms": 180ms. M-8 files
  Saft's 110ms provisionally (this seat's ledger, and §5's 180ms
  sentence may be describing the *composite* — strike + numeral→
  tick — rather than the wipe alone), but the three numbers are
  three files' numbers and REGISTER does not contain this
  constant. It must be registered, by Sana, before build.
  (DONE's title strike has the same disease in miniature: Saft
  140ms vs Druck/Zeile 180ms — M-11 files 140ms, same flag.)
- **U3 — DONE fall/landing bands for off-cap minutes.** The fall
  table (M-11) and landing tiers (F-12) are specced at the four
  cap durations (15/25/45/90). Tasks can carry other minutes
  (a 30M or 60M task exists in the data). No named source states
  the band-assignment rule, and F-12's per-tier *amplitudes* are
  likewise unstated except Ton's middle-tier `[0,18] @ [0,200]`.
  A nearest-cap-at-or-above lookup exists in this seat's
  invention file (saft-tasks §2) but it is **unmerged** and is
  not smuggled in here as law. *Sana/Ton to close.*
- **U4 — Promotion's sound is not in Ton's SCORE.** Zeile Rung 3
  files `playMechanicalLatch` for promotion (F-15). Ton's 30-row
  score has no promotion row, and the latch is elsewhere being
  retired (C3) or convicted (C4). Either Ton scores promotion
  (latch or a named replacement) or promotion ships haptic-only.
  This draft does not extend the score on Ton's behalf.
- **U5 — Bank entrance curve + the catch-light pass.** Fach and
  Taste agree the durations (260ms in / 180ms out) and disagree
  in curve vocabulary — both file retired names (CurveB /
  EasingB), and Kin's registry retires CurveB without naming its
  successor for a *slot* (as opposed to a cap). Kin to re-issue
  the curve; durations stand regardless. Separately, Taste Rung
  4's staggered catch-light pass sits against Mara's wall law
  (light must never carry data) and Kin's second-mover ban: it
  encodes nothing, by its author's claim, but it *is* motion no
  hand caused. Filed in M-2 as specced; its life is the merge's
  call, not this draft's.
- **U6 — Saft invention's DONE freeze re-time (seat's own, flagged
  against itself).** This seat's invention file (saft-tasks §3.2)
  re-times ledger #3's 40ms from the landing frame to a *detach*
  beat at the drop's start, stamp inside it. That is one freeze
  re-timed, not a fifth — but it contradicts this draft's M-11/
  §4.F5 (filed plan: freeze at landing) and the invention's own
  fallback concedes the point is the merge's to charge. §4 as
  drafted keeps the filed position; if Sana adopts the Galley
  Drop, §4.F5 steps 2–3 must be re-struck in the same commit.

## 4.F10 Not admitted to this inventory

- **The Carry and the Shove** (saft-tasks §§4–5, this seat's
  inventions): drag-reorder and swipe-to-complete for queue rows.
  Unmerged invention-wave proposals, not in any source this draft
  was charged with; admitting them would add a second DONE road
  on this screen and re-open §4.F6. They stand or die in the
  invention merge, not here.
- **Luz's wave-1 per-strike freeze economy** beyond C5/C6: her
  buffering/coalescing *cadence* for rapid strikes survives in
  M-8 (it is input handling, not feel spend); her freeze numbers
  do not.

— Saft, Game-Feel, Designers cohort. Inbox read at start (no unread
letters; Kin's motion-grammar letter was already in `read/` from the
plan wave and its arrival-frame law is applied throughout). Wall post
and letter to Sana accompany this filing.
