# §5.3 THE VALVE AND THE TOKEN — seat draft for MASTER-PLAN §5

*Ton — Designers, sound & haptics (the SCORE). Drafted for Sana to merge.
§0 already cites this number twice: the banked hold is "spec §5.3" (§0,
his verdicts) and the token is "specced in §5.3" (§0.1-9). This draft is
that §5.3: Part A the valve as a sound/haptic instrument, Part B the
token behind it.*

**Governing order, applied throughout:** MASTER-PLAN §0 (the three
standards; his verdict: **the 60s hold is BANKED**) · Barriere's FINALS
for every valve number (HUMANS-PLAN §3 + `humans-plan/barriere-valve.md`,
definitive) · REGISTER.md for every registered constant — drafted to its
values; disagreements flagged in §C, never forked · TOKEN-SPEC.md FINAL
as folded (Sten + Freya reviews in) for the token · Freya's Ledger-Direct
(`invention/MAXIMPL-INVENTIONS.md`, Voss accepted) for how the token
reaches the surface.

**Supersession, one line, as ordered:** DESIGNERS-PLAN §7.2's flat 2:1
drain and its hatch grammar are SUPERSEDED where they conflict with
Barriere — the drain is Barriere's ramp (§A6) and the draining tail is
rendered as live fill receding, never hatched.

**Sealed-state law (this section, per §0.1-5):** while a session is
sealed, the valve is the running session's only operable element and
the token's only door. Nothing in this section offers a second
mid-session decision: the overlay's only actions are the grip itself
and `KEEP FOCUSING`. Token enforcement sits at the repository boundary
(§B5), never in the valve's rendering. PARK cannot open, chrome does
not change, and no sound in this section speaks inside a session except
the valve's own events and the break (§A10).

---

## PART A — THE VALVE AS AN INSTRUMENT

### A1. What it is

The valve tests one thing — *is he still sure after a minute?* — and
must never test grip endurance, aim, or steadiness. **60.0 held
seconds** are owed; wall-clock, grace, and drain time never count
toward the 60. Banks at **15 / 30 / 45**. The token (§B) is spent only
at 60.0 held seconds. The instrument is built for the worst hand it
will meet (Barriere's table: half-asleep 03:30 · pumped + shaky ~09:30
· trembling on test eve · depleted grip 19:30), not the average hand.

### A2. Geometry, contact, entry (Barriere §1.1, §1.6)

- **Bar: full screen width × 112dp**, 0dp side margins, bottom-anchored
  in the disarm overlay, inside the thumb arc (top edge ≈78% of screen
  height). REGISTER: the bar exceeds the valve touch floor (≥88dp) by
  24dp because the thumb flattens under a 60s load.
- **Spill apron: 16dp** above the bar's top edge, continuing off both
  side edges. Contact inside the apron counts as contact on the bar —
  under load the thumb rolls off the *top* edge first; the apron turns
  a roll into a slide, not a slip.
- **Contact law: presence, never position.** Any contact counts — any
  finger, thumb, knuckle, palm edge; multiple pointers = one contact;
  centroid is ignored entirely. A contact that *begins* outside
  bar + apron and slides in never starts or resumes a hold; a contact
  that begins inside stays live anywhere inside bar + apron. Drift,
  patch migration, and wet-thumb skids inside are not slips. Slip is
  defined once: **contact fully leaving the bar + apron**.
- **Two-act start (anti-grief, HPA-28):** starting takes (1) a tap on
  the entry strip on the running surface, opening the overlay, then
  (2) a contact that *begins* inside the bar + apron. A pocket, a palm,
  a phone set face-down can perform neither act in sequence. Accrual
  starts on the first contact frame — a start delay feels like theft
  to a shaking hand; the protection is the two acts, not a lag.
- **The other control:** the exit chip (§A9) sits **≥120dp above the
  bar's top edge**, ≥56dp tall, and is **inert while any contact is on
  the bar** — the holding hand's tremor can never tap the abort it
  didn't mean.
- **Entry continuity:** the entry strip is **56dp tall, full width, at
  the identical bottom coordinates** as the bar — the hand never
  relocates between deciding and holding. (Geometry conflict with
  DESIGNERS-PLAN §6's rest slot: §C-U2.)

### A3. Type law for the overlay (DESIGNERS-PLAN §7, Set — retained whole)

An overlay is read at panic distance: hero + body, nothing small.

- **Countdown: Sora 700 50sp** — one size for the whole countdown;
  digits change, size never does. It sits **above the bar**. Owed
  seconds step only (measured numbers step; they never roll through
  values that weren't true).
- **Split readout under the countdown: 15sp** — `BANKED 30 · HOLDING 7`,
  one verb-free state line. The state word is the whole message
  (Panik's verb sentence stays dead).
- **State word: Atkinson 700 15sp, +0.5 tracking. One human sentence:
  Atkinson 400 15sp.** No plate text inside the overlay except the
  model mark.
- Entrance class: **Decision 240ms**.
- *Flagged, drafted to REGISTER:* Barriere's file prints owed seconds
  at 56sp Anonymous Pro Bold with a 13sp secondary line. Both sizes are
  off the REGISTER type ladder (104 / 72 / 50 / 22 / 15 / 10 — off-ladder
  sizes are build errors), so the ladder + §7 type law govern the
  rendering and Barriere's *content* (owed seconds, the BANKED·HOLDING
  line) is carried at 50sp / 15sp. See §C-R5.

### A4. Accrual and the hand's timeline (Barriere §1.2)

State, exactly: `banked ∈ {0, 15, 30, 45}` · `held` = seconds accrued
above `banked` in the current contact · owed = `60 − banked − held`.
Accrual runs only while contact is live, at 1.0×, sampled on the
existing 32ms loop. The fill is **direct-drive linear** (Kin M-19) —
an eased timer is a lying timer.

- **Per-second tick:** haptic `[0, 10]ms @ 140 (≈0.55)`, **sound none**.
  (Wave-1's 4ms ticks sit under the tremor noise floor; 10ms @ 0.55 is
  felt through a shaking hand. This replaces the SCORE's inter-bank
  tooth — 1,600Hz / `[0,3] @ [0,80]` — on this surface: §C-R3.)
- **5-second metronome:** haptic `[0, 12]ms @ 205 (≈0.8)`, replacing
  that second's tick. Twelve pulses = the minute, countable in the
  thumb.
- **Final leg (45→60):** the tick grows to `[0, 12]ms @ 180`; at **55s
  each remaining second's tick doubles** (second pulse +100ms). The
  last five seconds are countable without looking. Chamber 4's walls
  step brighter at each of the last 5 seconds (100ms step each).
  **Drain rules do not change in the final leg** — no sudden strictness
  at the finish line.
- Sync law (SCORE, standing): haptic onset and sound onset within
  **±8ms**; the visual pair lands in the same 100ms window.

### A5. The bank frame (DESIGNERS §7.2 grammar × Barriere's step)

At each bank crossing, everything lands on **one frame** — the frame
is the Designers grammar; the step's contents are Barriere's:

1. chamber wall **3→5dp** (§7.2) and chamber floor line **1dp→3dp**
   (Barriere) — the chamber's edges thicken as it becomes structure;
2. chamber fill **hard-cuts to solid banked** — solid + toothed =
   banked and safe; smooth + alpha = live and at risk;
3. an **8dp step-tooth drops** from the chamber's top edge;
4. the next notch fills; the margin word **re-strikes**
   (`BANKED 15 / 30 / 45`);
5. the **40ms bank-catch freeze** (Saft's ledger; REGISTER: one of
   exactly four freezes app-wide);
6. sound + haptic per the table below.

**Pitch falls, weight rises** — each bank is a heavier bolt seating
lower in the door. The LADDER set for this instrument is therefore
Barriere's fixed set, and it **replaces the SCORE's LADDER set**
(A3 220 / C4 261.63 / E4 329.63, rising) on this surface:

| Bank | Haptic (ms / amplitude 0–255) | Sound | dB vs seal (Ton, derived) |
|---|---|---|---|
| **15** | `[0, 12, 28, 10] / [0, 215, 0, 160]` — one heavy latch + echo knock | 560Hz transient, 16ms, gain 0.54 | ≈ −5.4dB |
| **30** | `[0, 14, 22, 11, 20, 12] / [0, 235, 0, 175, 0, 155]` — double seat | 470Hz, 22ms, gain 0.58 | ≈ −4.7dB |
| **45** | `[0, 17, 26, 13, 22, 14] / [0, 255, 0, 200, 0, 170]` — triple seat, heaviest | 390Hz, 28ms, gain 0.62 | ≈ −4.2dB |

(The dB column is my seat's conversion of Barriere's linear gains
against the seal = 0dB reference; the gains and waveforms are his,
verbatim. Fixed per bank = a timbre grade — a bank sounds *heavier* —
which is the form Sana's merge line allows; pitch as melody/formula
stays dead. Barriere's within-segment pitch climbs — 420→660Hz per
segment, 520→780Hz final leg — are pitch computed from progress, a
formula under DESIGNERS §1.6 and the SCORE §0.3, and are **not
drafted**; the per-second ticks above are unpitched in the hand and
silent in the ear. Flagged §C-R3 for Sana's merge call.)

**No accent anywhere in the break path** (§11-R16; chambers travel in
ink/glyph and stamp solid ink — breaking is not a commit).

**The Bank Beat (HPA-27):** at each bank crossing, grace restarts in
full (§A6) — if he releases inside the beat, nothing drains. The valve
proves at 15s that stopping loses nothing; permission, not friction.

**Floors never drain in-session.** A bank lives while the overlay is
open and across dismissal/re-entry within the same session (the bank
is the session's, held in the ViewModel — Freya's machine); it clears
only on (a) completion, (b) an explicit exit-chip tap — a deliberate
surrender, stated in its caption — or (c) the session ending on its
own. It never expires on a hidden timer.

### A6. Slip and drain (Barriere §1.3 — definitive, verbatim numbers)

1. **0–800ms after contact loss: no drain, no accrual.** The grace
   beat. 800ms is the longest common involuntary gap (tremor
   micro-lifts and elbow bumps run 200–600ms). **Grace is per-slip
   and uncapped** — grace never accrues a held millisecond and the
   phone stays sealed throughout, so a cap would punish only the
   involuntary case to prevent an exploit that gains nothing.
2. **Drain ramp: 1.0× for the first 3.0s of drain → 2.0× thereafter,**
   easing back as the floor catch approaches. Re-grip lands at
   ≈2.5–3.5s; a hand back at 3.0s has lost **≈2.2s** of tail — a
   breath. A walk-away loses the whole tail: worst tail (≈14.9s
   unbanked) reaches the floor in **≈9.7s** from the slip
   (0.8 grace + 3.0 @ 1.0× + ≈5.9 @ 2.0×).
3. **Floor = the last bank, full stop.** Drain halts dead at `banked`;
   banked seconds never drain inside the session. If `banked = 0`,
   the floor is 0. No zeroing clause exists — a release is exactly
   what the drain already prices.
4. **Re-grab:** drain halts within one 32ms frame of new contact;
   accrual resumes after a **250ms re-seat** (the re-seat itself does
   not accrue). Re-grab during grace: nothing was lost; the same
   250ms re-seat applies. A skid the sensor reports as a "new" touch
   beginning inside the bar within the grace window is a continuation
   under this rule, never a fresh start.
5. **OS cancels** (call UI, system gesture) are slips — grace + drain —
   never completions, never instant zeroes. If the system dismisses
   the overlay mid-hold, the bank persists for re-entry within the
   same session (§A5).

**The drain is felt, not watched:** during drain, a haptic pulse every
**250ms** — `[0, 6]ms`, amplitude falling linearly **150 → 60** as the
tail empties — over a descending sound glide **600Hz → 280Hz**, gain
0.34 → 0.18. At the floor: a **catch** — latch waveform
`[0, 11, 26, 9] / [0, 215, 0, 150]` at ×0.7 gain, 520Hz. *The floor is
a click in the thumb.* If the floor is 0, the drain ends with the
latch-release pattern instead — nothing caught, because nothing was
banked; the hand hears the difference. (This voicing is Barriere's and
supersedes the SCORE row 27's descending-LADDER-at-floors voicing,
which could never fire under a floor that never drains: §C-R3.)

**Drain-stop rendering (Floor's requirement, §7.2 — placed inside the
valve, per Freya's one-rendering rule):** when the drain stops at the
floor, the valve's own centre line states `SLIPPED — BANKED {n} SAFE`
for 2s. A silent drain is a number that changed while he was looking
away; the overlay card renders nothing about the slip — the slip's
entire rendering lives inside the valve.

### A7. Completion — 60.0 held seconds (Barriere §1.5)

At exactly 60.0 accumulated held seconds, on one frame:

- **Haptic:** the disarm seal — `[0, 18, 30, 12]ms / [0, 200, 90, 140]`
  — followed after a 60ms gap by one terminal thock `[0, 120]ms @ 255`.
  Nothing after. Once, unreplayable.
- **Sound:** harmonic seal (disarm variant), 48ms, 480Hz, gain 0.62 —
  fired **once, by the ViewModel, only on `Completed`, after the
  transaction commits** (§B5, Amendment B). The valve view plays
  nothing at completion (Freya buildspec point 6: the view's own
  completion feel is deleted — two sounds for one event is a flam,
  and a flam is a bug). Then the **300ms silence moat** (§7.2): no UI
  sound may speak inside it.
- **Visual:** all four chambers solid + toothed for 180ms; the bar
  compresses **112dp → 108dp over 140ms** (a thing seating under
  load); then the overlay exits straight to the idle Deck.
- **The spend books in the same frame** — the repository transaction
  of §B4.1 is this frame's other half: token flips and seal land
  together or not at all. Banking never spent anything along the way;
  only this frame spends.
- The break reads as *breaking*, never as finishing: no stamp, no
  strum, no completion pulse, no receipt shares this frame (§B4.5).

*Superseded on this event:* the SCORE row 28's crack + seal-release
stack (two heavy feels, −2dB) dies — Barriere's completion is one seal
and one thock. The moat survives because Barriere is silent on what
follows the thock, and the moat is after it. (§C-R3.)

### A8. The spent valve — stillness, and `BREAK SPENT`

When the week's token is spent, the valve's SPENT state is its **only
standing rendering** (§B4.5):

- **The valve is not composed** (Freya, Ledger-Direct — §B5). In its
  slot the overlay composes a **SPENT plate**: same track/seams
  geometry, no touch handling. Touch refusal becomes absence, not a
  check — a gate that is a missing object cannot be bypassed by a
  stale boolean.
- **A spent valve answers with stillness** (§7.2): a full-size printed
  fact, **zero touch acknowledgement** — no sound, no haptic, no press
  visual, ever. It is information, not a door; the overlay still opens
  so the fact can be read.
- **`BREAK SPENT` — its two placements, and only two** (§7.2 + §1.3):
  (1) on the spent valve's slot at rest on the running surface (Old
  Tom's condition — the valve prints the fact on itself), and (2) on
  the SPENT plate inside the emergency overlay. Word-only, night-only
  **#FF8A5C**, ≤2% area, **never on AOD**; day gets the same words in
  glyph ink with zero colour placements. No other surface prints it —
  not the Drawer, not a receipt, not chrome.
- The overlay in SPENT carries Freya's verbatim tokens=0 body (§B7)
  and the exit chip as its only action.

*Word collision on the plate, flagged not resolved:* Freya's buildspec
point 8 gives the plate its own valve-drawn words (`NO BREAKS LEFT` /
`THE WEEK'S ONE BREAK IS SPENT`). Those words and `BREAK SPENT` cannot
both be the plate's hero fact. This draft prints `BREAK SPENT` per the
Designers overlay law I was seated to draft; the collision is Sana's
merge call (§C-U1).

### A9. Overlay states and the exit chip (Freya's machine, as amended)

Owner split: **ViewModel owns the bank · the view owns the grip · the
overlay owns the visit.** No state lives in two places.

- **OPEN_IDLE** (no contact): chip live; nothing else moves.
- **HOLDING** (grip down): the chip **dims to 35% alpha and is inert**
  — the finger on the valve is the only input the card accepts
  (Barriere's inert-while-contact law, same rule, wider statement:
  his ≥120dp placement in §A2 stands alongside).
- **GRACE / DRAINING:** the chip **stays live** — abandoning the
  attempt is a legitimate act. Dismissing settles `held` to the bank
  floor; the bank is kept. The card renders nothing about the slip
  (§A6).
- **COMPLETED:** §A7. Overlay closes on `fadeOut(specA)` straight to
  the idle Deck. **No receipt.**
- **ABANDONED** (chip tap, or system Back — Back routes to dismiss =
  keep focusing, the safe direction): overlay closes, bank persists
  in the VM, nothing spent, **silence**. Reopening greets him
  truthfully: owed = 60 − banked.
- **SPENT:** §A8. Entered at open when remaining = 0, or **in place**
  mid-visit on `RefusedNoToken` or a 1→0 rollover emission (§B5,
  Amendments B/C as implemented by Ledger-Direct's composition
  switch): body swaps to the tokens=0 copy, the plate replaces the
  valve, the chip remains the only action, the overlay does **not**
  close, **no sound plays**, and the attempt's bank is neither spent
  nor cleared — it freezes behind the absent valve and dies with the
  session.
- **Chip copy:** `KEEP FOCUSING` — TOKEN-SPEC §7 verbatim (the
  "(STAY ARMED)" parenthetical is dead). Barriere names the same
  control `STAY LOCKED`; the position and inertness law above is his,
  the words are §7's FINAL text. Name collision flagged (§C-U3).
- The scrim has **no outside-tap dismiss**. The only exits are the
  chip and completion. The overlay never touches window brightness or
  `FLAG_KEEP_SCREEN_ON` — those belong to the desk clock alone.

### A10. Silence and night (the SCORE's standing law, applied here)

- **Session-running mute:** inside a session the only sounds are the
  valve's own events (§A4–A7), the airlock Catch + peek expiry, and
  the finish. The valve section adds no other voice.
- **Refusal = silence.** A refused break, a refused grip on a spent
  plate, an abandoned attempt: silent. No error buzz exists in this
  app; Android's stock double-buzz is banned by name.
- **Night:** all sound gains **×0.6**, all haptic amplitudes **×0.85**
  (minimum tick amplitude 70 — below that, drop the tail segment,
  never the strike). Night values are day values scaled, never
  re-voiced. Ringer-silent: haptics only. DND: haptics only for direct
  finger gestures — every event in Part A qualifies. SOUND EFFECTS
  sled off: both components off, no exemptions.
- **The first 3 seconds decide it** (Panik, via Humans §3): the lawful
  60s door is slower than the Settings→uninstall exit (~30s), so at
  overlay open the state word is already present and the first tick is
  felt on the first contact frame — the entry must read as already-his:
  no surprise, no warm-up.

---

## PART B — THE TOKEN BEHIND THE VALVE (TOKEN-SPEC, FINAL as folded)

### B1. The token is a fact, not a counter — the spend ledger

The token is **derived from an append-only spend ledger**, never held
as a number anywhere. New Room table `emergency_break_spends` — no
update, no delete path anywhere in the app:

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `sessionId` | INTEGER NOT NULL | the `FocusSessionEntity.id` this break ended; the join key |
| `spentAtWallMillis` | INTEGER NOT NULL | wall-clock instant of completion |
| `weekAnchorMillis` | INTEGER NOT NULL | the Monday-00:00 anchor (§B2) of the week this spend counts against — **stored at spend time, never recomputed** |
| `heldSeconds` | INTEGER NOT NULL | always 60 at completion today; the record says what was paid, in the valve's own terms |

No other columns — **no attempt rows, no bank rows, no counter row**
(a counter row cannot render history; a ledger can). DAO additions,
names final (TOKEN-SPEC §2): `insertEmergencyBreakSpend` ·
`getSpendsForWeek(anchor)` · `observeSpendsForWeek(anchor)` ·
`getAllSpendsSnapshot()`.

The only formula in the system:

```
remaining(now) = max(0, 1 − getSpendsForWeek(weekAnchorMillis(now)).size)
```

**TOKEN: exactly 1 per week** (REGISTER).

### B2. The week anchor — Monday 00:00

One shared definition, `MonolithWeek.weekAnchorMillis(nowMillis,
zone = ZoneId.systemDefault())`: the instant of **Monday 00:00:00.000
local civil time** of the week containing `now`, in the device's
current zone (TOKEN-SPEC §3 formula: `previousOrSame(MONDAY)` →
`atStartOfDay(zone)`). Consumed by the token and by any weekly
rendering — one fact, one definition. A spend counts against the
current week iff its **stored** anchor equals the anchor computed at
evaluation time. Rollover needs no job, no reset, no alarm: at Monday
00:00 the equality simply stops matching last week's rows.

Edge cases, ruled in TOKEN-SPEC §3 and carried whole: **travel /
timezone change** — anchor always computed in the current zone, stored
spends keep their booking instant; a zone change shifts the effective
refill by at most the zone difference (bounded ≈26h, intercontinental
only, resolving at the next true local Monday) — accepted deliberately,
zone-of-spend bookkeeping would be a second definition of the week.
**DST** — `atStartOfDay` java.time gap/overlap rules (Asia/Kolkata has
no DST; the rule keeps the code correct in any zone). **Clock changes**
— wall clock only can name a Monday; a clock set forward refills early
(out of scope: his phone, his discipline tool), but spends are rows and
moving the clock back deletes nothing. **Scope** — `MonolithWeek` does
not replace `RoutineSchedule`; spends attribute by *completion*
instant (§B3.2), routine windows by *start* day — different facts,
never week-joined; the Monday refill resets no routine streak
(Sten, Charge 3). **Direct Boot** — the DB lives in device-protected
storage, so `remaining()` is computable before first unlock.

### B3. Spend semantics

**B3.1 The only write.** At valve completion, and nowhere else, the
repository runs **one transaction**, `completeEmergencyBreak():
BreakResult` (sealed type, exactly three outcomes):

1. `active = dao.getActiveSessionSnapshot() ?: return NoActiveSession`
2. `if getSpendsForWeek(currentAnchor).size >= 1 return RefusedNoToken`
   — the session stays sealed.
3. Insert the spend row (`sessionId = active.id`, `spentAtWallMillis =
   now`, `weekAnchorMillis = currentAnchor`, `heldSeconds = 60`).
4. Disarm, dispatched on the active session's kind, inside the same
   transaction: a *routine session* books by Sten's attendance outcome
   (ATTENDED as booked / PARTIAL via `disarmRoutineSessionPartial` /
   MISSED via `disarmRoutineSessionUnattended`, `finalizeAttendance`
   in-transaction; SLEEP-loadout routines exempt — generic path, no
   finalise); *any other session* disarms via
   `disarmActiveSession(completed = false, outcomeReason =
   EMERGENCY_BREAK_REASON)`. The generic path's wall-clock booking
   (Lie #13) must never run on a routine session (Sten B1).
5. `return Completed`.

Atomicity is the point: no interleaving where the session ends but the
spend is lost, or the spend lands but the session stays sealed; step 2
inside the transaction kills the double-completion race. **SPEND:
written only at 60.0 held seconds, in the same transaction as the
disarm** (REGISTER).

**B3.2 The boundary race.** A hold that starts Sunday 23:59 and
completes Monday 00:01 spends against **the anchor of the completion
instant** — the new week pays. Ruled; no builder improvises otherwise.

**B3.3 Attempts write nothing — verified, now required** (HPA-28).
Bank crossings (15/30/45) write nothing — banks are view/VM-local feel,
not records. Slips, drains, abandoned attempts, OS cancels write
nothing. The overlay merely *opening* writes nothing. **No attempts
counter may be added** — not for analytics, not for the record. This
silence governs `emergency_break_spends` and token accounting only: it
does **not** reach `routine_attendance` — a hold started inside a
routine window stamps E4 attendance evidence per Sten §5.3 (presence
in a scheduled window, not a valve attempt), and E4 evidence is never
an input to `remaining()` (Sten B4). The record of the valve is binary
per week: a spend row, or silence. (Session records written by the
disarm itself — including the Partial Stamp for a broken session's
attended time — are session records, not attempt records, and are
unchanged.)

**B3.4 Grace cancel is not a break.** The "Accidental Start Grace
Cancel" path is untouched: no spend row, no spend check, existing
zero-booking preserved. It never passes through
`completeEmergencyBreak()`.

### B4. The record of a break — Sana's reconciliation, stated

**B4.5 (TOKEN-SPEC §4.5 as amended by this ruling).** The spend row
**is** the record — `sessionId` is the join between the spend ledger
and the session row, and the break is renderable from that join, never
from string-matching `outcomeReason`. The outcome-reason constant for
this path is exactly `"Emergency Break"` (`EMERGENCY_BREAK_REASON`).

**The reconciliation Sana has ruled, stated as law for this section:**
TOKEN-SPEC §4.5's spend rendering *as a tab row* is **SUPERSEDED by
the Drawer ruling** (`plan/GUARD-RULING-AND-DRAWER.md`): the Drawer
stores **finished tasks only**; sessions and holds are not Drawer
content, and no spend row, break line, or session row is rendered in
the Drawer. What survives of §4.5 is the record itself, in its two
data homes — **the spend ledger row + the session row it joins** —
and exactly one standing rendering of it anywhere in the app: **the
valve's SPENT state** (§A8). TOKEN-SPEC §7's final bullet (a week in
the rebuilt Ledger showing the spend as a fact of that week; UNSPENT
as a row) falls with the same ruling — UNSPENT survives only as a word
for the DAY CLOSED frame per Humans, never as a row of its own.

**No finish receipt for a broken session** (Freya, Charge 2 — folded):
the receipt's trigger is `completedSuccessfully` only; a valve break
is booked `completed = false` (§B3.1); completion and emergency never
share an exit form — no pulse, no DONE word, no stays-until-tapped
face. Record-without-receipt is the established form for an exception
path (grace cancel is the precedent). The partial is stated once, in
the record — not as a keepsake on the Deck.

### B5. Enforcement — the repository is the law; Ledger-Direct at the surface

- **Repository surface (names final):**
  `emergencyBreaksRemaining(nowMillis): Int` ·
  `observeEmergencyBreaksRemaining(): Flow<Int>` ·
  `completeEmergencyBreak(): BreakResult`. The repository refuses a
  break at zero regardless of what any UI asks; the UI number is a
  rendering, the repository is the law.
- **ViewModel:** `_weeklyEmergencyTokensRemaining` (the bare in-memory
  `MutableStateFlow(1)`) is **deleted** — today any process death
  silently refills the week's break; that is the debt in its exact
  shape. The VM exposes the repository flow as state.
  `confirmDisarmSession` loses its token block entirely and never
  touches the token again. The overlay's completion callback routes to
  a VM method calling `repository.completeEmergencyBreak()`, running
  the shield-stop/stats refresh only on `Completed`.
- **Ledger-Direct (Freya's invention, Voss accepted — governs the
  valve's input):** **the valve receives no count.** No token state
  exists in the view layer at all. The *overlay* takes
  `breaksRemaining` as a Compose parameter derived from
  `observeEmergencyBreaksRemaining()`; the valve is composed only at
  `remaining == 1`, carrying only `requiredSeconds` and
  `onEmergencyBreakCompleted`; **at 0 it is not composed** — the SPENT
  plate renders instead (§A8). Its `onDraw` loses both token branches
  (the spent label and the `${tokens}/1 LEFT` badge are deleted — the
  count's one home is the surface that derives it; the valve keeps
  mechanics only). Completion is the only valve→system signal, and
  the repository is its only judge: the wasted-hold scenario is
  unreachable by construction, not by a guard.
- **Amendment A — derivation, not snapshot (stands):**
  `DisarmChallengeState.weeklyEmergencyTokensRemaining` is never
  written at open time; overlay state is derived from the flow for as
  long as the overlay is visible. A count rendered in an open overlay
  is the repository's current truth or it is not rendered at all.
- **Amendment B — the Refused transition (stands, implemented as
  Ledger-Direct's composition switch):** on `RefusedNoToken`, the
  overlay transitions in place to SPENT (§A9) — silent, bank frozen,
  overlay open. One mechanism serves open, rollover, and refusal.
- **Amendment C — rollover during an open visit (stands):** the flow
  must emit on a week-anchor change while collected (a midnight tick,
  not only on collection or re-read). Transitions apply only while
  the valve is not gripped; during HOLDING / GRACE / DRAINING nothing
  on the surface changes mid-attempt. Under Ledger-Direct this is
  deferral, not a setter behaving itself: the view reports
  attempt-liveness upward via `onAttemptLiveChanged(Boolean)` (grip
  start / drain start = true; drain-to-floor / dismiss / completion =
  false), the overlay holds the pending composition switch until the
  callback reports false, and `completeEmergencyBreak()` decides the
  completion under §B3.2. The rendering never interrupts a grip.
- **Amendment D — demoted to fallback, by its own author's hand:**
  with no count input there is no setter to make pure; D is **moot
  under Ledger-Direct** (Voss's accepted fold) and stands only as the
  fallback if Ledger-Direct ever loses: any future count setter gates
  touch and rendering only, and must never call `resetHold()` or touch
  held, banked, or drain state.
- Any other present or future caller of a break-class disarm must go
  through `completeEmergencyBreak()`; `disarmActiveSession` with the
  `"Emergency Break"` reason from anywhere else is a build error by
  convention. The stopwatch clean-completion path (`completed = true`)
  and routine BOUNDARY_END disarms never interact with the token,
  except a break ending a routine session — only through §B3.1's
  dispatch (Sten B2).

### B6. Migration 7→8 — TOKEN-SPEC's alone, CLOSED (REGISTER arbitration)

Adding the entity takes the database **7 → 8**, and this migration is
the spend table's alone — nothing else rides (Voss, binding; REGISTER).

1. **Explicit `Migration(7, 8)`** registered in
   `MonolithDatabase.getInstance`, creating `emergency_break_spends`
   exactly as §B1 (TOKEN-SPEC §6 SQL verbatim).
2. **`fallbackToDestructiveMigration(true)` is DELETED in the same
   change** — permanently. After it, a missing migration is a loud
   `IllegalStateException` in testing, never a silent wipe of
   sessions, routines, and spends on his phone. (This supersedes
   Sten §3.6's sentence permitting the fallback to remain — Sten A3.)
3. **No backfill.** Today's in-memory spends were never recorded, so
   v8 starts with an empty ledger and a full token; inventing a past
   spend would fabricate a record. Consequence, disclosed not hidden:
   on the upgrade week only, a break spent pre-upgrade is not
   remembered. One week, once.
4. **Chain:** the routines rebuild takes **8→9** (Sten's 13 ADD
   COLUMNs + `startedAtSessionElapsedSec` + `taskId`;
   `autoPausedAtMillis` is formally NOT created) and the plumbing
   batch **9→10** (Rurik + Otis, append/child tables only) — REGISTER.
   The routines reseed is not a Room migration callback: it runs in
   `ensureSeededAndSyncPackages` under `seedMutex`, guarded by
   `routineReseedV1Done`, version-number-blind (Sten A1/C1). Both
   migrations stay registered permanently; a direct 7→9 upgrade runs
   the chain 7→8→9 in order.

### B7. Surface contract (TOKEN-SPEC §7 — Freya's sentences, verbatim)

- Every rendering of the count traces to `remaining()` — the Deck chip
  (`$n/1 WEEK`), the disarm overlay, the valve's composed-or-absent
  state. One number, one source. No surface holds, decrements, or
  refunds it.
- The overlay's sentences are **Freya's buildspec point 2, verbatim,
  incorporated by reference**: title `EMERGENCY BREAK` · body at 1
  remaining: *"This ends the session early. It is the only break this
  week."* · body at 0: *"This week's break is already spent. Sessions
  end when their time does."* · rule line under the valve: *"Hold to
  fill 60 seconds. Every 15 seconds banks. If your hand slips, what
  isn't banked drains away. The banked part stays."* · chip
  `KEEP FOCUSING`. This section changes no word of them; no builder
  copy is sourced from here.
- The shipped body sentence ("Sessions are unbreakable by tap. You
  have N weekly emergency break token remaining. Hold the physical
  valve continuously for 60 seconds to abort.") is false twice over —
  continuity died with the banked verdict, and it presents the token
  as a thing the UI holds — and is **deleted wherever it appears**.
  The words "continuously" / "continuous" may not appear in any valve
  or overlay copy. One job per element: the body's job is consequence,
  the count's home is the deriving surface (§B5), the rule line's job
  is mechanics.
- Under the Drawer reconciliation (§B4), this contract's Ledger
  bullet is the superseded one named there; the rest of §7 stands
  whole.

### B8. Acceptance — the build is done when these pass

*From TOKEN-SPEC §8 (the token's nine, verbatim in substance):*

1. `MonolithWeek` unit tests: ordinary Monday; Sunday 23:59 vs Monday
   00:00 in Asia/Kolkata; a DST zone (America/New_York, spring-forward
   week); a zone-change pair (same instant, two zones, anchors differ
   by the ruled bound).
2. Remaining-derivation: no spends → 1; one spend this anchor → 0; one
   spend last anchor → 1; two spends same anchor (corruption drill)
   → 0, never negative.
3. Refusal: at 0 remaining, `completeEmergencyBreak()` returns
   `RefusedNoToken`, the session row stays active, **no** spend row is
   inserted.
4. Atomicity: forced failure inside the disarm step leaves no spend
   row; forced failure at insert leaves the session sealed.
5. Attempt-silence: simulated bank crossings + slip + abandon produce
   **zero** rows in `emergency_break_spends` and zero repository
   writes.
6. Migration: a v7-shaped database with session/routine rows opens
   under v8 — rows intact, spends table present and empty; the builder
   no longer contains `fallbackToDestructiveMigration`.
7. Restart: spend, kill the process, relaunch — remaining is 0. (The
   exact scenario the current build fails.)
8. Chain (when the routines landing comes, added to — never replacing
   — test 6): v7 → v9 via the registered chain with routine rows
   intact and spends present; v8 (spend rows present) → v9 with spend
   rows intact. *(Sten A2.)*
9. Routine-break: valve completion inside a routine window books the
   spend against that session's id, books attended/partial/zero per
   Sten §5.4 (never wall-clock), writes no Partial Stamp intercept
   log, and finalises attendance in the same transaction — a forced
   failure at finalise leaves no spend row and the session sealed.
   *(Sten B3.)*

*The valve's own checks (Barriere + Designers §7 + this seat):*

10. Drain timing on device: slip at 14.9s unbanked → floor at
    ≈9.7s (±1 frame at each ramp joint); re-grip at 3.0s post-slip
    has lost ≈2.2s; grace ≤800ms loses 0.0s, at any bank, any number
    of consecutive slips.
11. Bank frame synchrony: at 15/30/45 the solid cut, tooth drop, wall
    + floor thickening, margin re-strike, haptic onset, and sound
    onset all land on one frame (±8ms haptic/sound); pitch measured
    at the transient reads 560 / 470 / 390Hz by bank — falling, never
    rising.
12. Eyes-off run: screen covered, full 60s completed by feel alone —
    12 metronome pulses counted, three banks named blind, floor catch
    felt on a forced slip, completion thock unambiguous. If any bank
    cannot be named blind, the waveform asymmetry widens, never
    narrows.
13. Spent stillness: at remaining = 0 the valve view is absent from
    composition (Ledger-Direct — asserted in the UI tree, not by a
    boolean), the SPENT plate renders, and 20 taps on the plate
    produce zero sounds, zero haptics, zero state changes.
14. No-receipt: a completed break exits `fadeOut(specA)` to the idle
    Deck with the seal as its only sound; no receipt face, pulse, or
    DONE word is reachable from the break path in any state.
15. Mid-visit rollover: overlay held open across Monday 00:00 — no
    surface change during a live grip; on grip release the pending
    composition switch applies (Amendment C by deferral).

---

## §C — CONFLICTS: reconciled, and unresolvable

**Reconciled (resolution mechanism named for each):**

- **R1 — Drain rate + rendering.** DESIGNERS §7.2 flat 2:1 + hatch vs
  Barriere's 800ms-grace → 1.0×/3.0s → 2.0× ramp + live fill.
  Resolved by the task's own supersession rule: Barriere governs
  (one-line statement at this draft's head). Critics' reversed
  citation of the same numbers is likewise superseded (REGISTER).
- **R2 — §4.5's tab row.** TOKEN-SPEC §4.5 (Ledger tab row) vs the
  Drawer ruling. Resolved by Sana's ruled reconciliation, stated in
  §B4: no Drawer row; spend ledger + session row are the record; the
  SPENT state is the only standing rendering; no finish receipt.
- **R3 — My own SCORE, three places.** (a) LADDER set rising
  A3/C4/E4 → Barriere's falling 560/470/390 fixed set (banks as
  timbre grades — Sana's merge line to me, 0535: fixed sets survive
  as grades, pitch-as-melody stays dead). (b) Row 27's drain voicing
  (descending ladder at crossed floors) → Barriere's 250ms pulses +
  glide + floor catch; the old voicing was also internally dead,
  since floors never drain. (c) Row 28's crack/stack completion →
  Barriere's single seal + terminal thock, moat retained. (d)
  Inter-bank tooth (pitched-adjacent, `[0,3]`) → Barriere's unpitched
  10ms @ 0.55 tick + 12-pulse metronome. Barriere's within-segment
  pitch *climbs* are excluded under the formula ban (§A5) — flagged
  here so Sana can overrule me in the open if she reads Barriere's
  "definitive" as covering them.
- **R4 — Amendment D vs Ledger-Direct.** Freya killed her own
  amendment by mootness; Voss accepted both folds. Drafted as:
  Ledger-Direct governs, D retained as named fallback (§B5).
  TOKEN-SPEC §5's "the valve view still receives a plain
  `tokensRemaining: Int`" is struck by the same fold.
- **R5 — Countdown typography.** Barriere 56sp Anon Pro / 13sp line
  vs DESIGNERS §7 (50 Sora / 15sp) vs REGISTER ladder. Drafted to
  REGISTER (its rule: register values govern, others are superseded
  interims), Barriere's content carried at ladder sizes (§A3).
- **R6 — Freya's slot geometry.** Buildspec point 1 (144dp slot, 88dp
  bar, valve-drawn 44sp numeral) vs REGISTER bar (full-width × 112dp)
  + §7 type law (countdown above the bar, 50sp). Drafted to REGISTER
  + §7; point 1's *intent* (the bar never shrinks; numeral/bar/line
  can never disagree) is preserved by the overlay owning all three.
- **R7 — The exit chip's inertness.** Freya (dims + inert in HOLDING,
  live in GRACE/DRAINING) and Barriere (inert while any contact is on
  the bar, ≥120dp above it) state one law at two widths; drafted as
  the union (§A2, §A9) — no numbers conflict.
- **R8 — `SLIPPED — BANKED {n} SAFE`.** Floor's line (§7.2) vs Freya's
  "the card renders nothing about the slip." Both stand if the line
  lives where Freya puts all slip rendering: inside the valve.
  Drafted inside the valve, 2s, at drain-stop (§A6).

**Unresolvable by this seat — named, not invented around:**

- **U1 — The SPENT plate's hero words.** `BREAK SPENT` (DESIGNERS
  §1.3/§7.2, two placements, night #FF8A5C) vs Freya buildspec point 8
  (`NO BREAKS LEFT` hero + `THE WEEK'S ONE BREAK IS SPENT` centre
  line). Both claim the same slot as the spent fact. Drafted with
  `BREAK SPENT` per my assigned overlay law; Sana rules which word
  dies. TOKEN-SPEC §7 is silent here (its verbatim set is title /
  body / rule / chip only), so neither text outranks the other from
  inside Part B.
- **U2 — The rest-state entry geometry.** DESIGNERS §6: recessed
  200×48dp slot nested in the running arc's gap, plate `HOLD TO
  BREAK`. Barriere §1.1: 56dp full-width entry strip at the bar's
  identical bottom coordinates (hand never relocates). Both cannot be
  the running surface's valve rest state. Part A drafts Barriere's
  entry continuity because the valve's FINALS are his; the running
  screen belongs to §5's other seats — Sana's merge call across
  drafts, not mine inside this one.
- **U3 — The exit chip's name.** `STAY LOCKED` (Barriere — also not in
  Teen's closed verb list, where KEEP is dialogs-only and the chip is
  an overlay act) vs `KEEP FOCUSING` (TOKEN-SPEC §7 verbatim, FINAL
  as folded; Freya point 2). Copy drafted as `KEEP FOCUSING` because
  §7's fold is the later, reviewed instrument; flagged because
  Barriere's file is "definitive" for the valve and names the control
  he specced. One word from Sana or Voss closes it.
- **U4 — Natural completion's sound (inherited, not mine to close).**
  Freya's buildspec leaves it unconverged: if natural completion and
  a valve break both sound `playHarmonicSeal(arming = false)`, break
  and completion share a voice, against her §4's "never share an exit
  form, sound included"; REGISTER reserves the completion pulse for
  verified fully-elapsed sessions. Part A depends on it only at one
  point — §A7's break voice is Barriere's seal, whatever completion
  gets — but the *distinctness* claim in §A7 cannot be finally true
  until Voss/Ines/Freya close it. Carried open, owned by name.

*— Ton, Designers (haptics & sound). No code touched; this draft,
one wall post, one letter to Sana are the whole delivery.*
