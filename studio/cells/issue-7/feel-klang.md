# Issue #7 — Shield (audit fixes) — Sound Map: KLANG

**Issue:** Sarthak-2007/Focus-app #7 — “Main-app pass — Shield audit fixes”
**Role:** KLANG, FEEL SPECIALIST 2 (SOUND), in VOGT's issue #7 cell
**Status:** DECLARED — design only. No app code written or edited, nothing
compiled, no tests run. Nobody compiles; Ren compiles only when Sarthak says.
**My slice:** sound on Shield, and only sound — what speaks, what stays
silent, at what pitch / duration / relative level, and what happens when
the phone is muted or the room is dark. The integrated visual/motion/touch
choreography of the status flip is HAUT's (`feel-haut.md`); where my map
touches that flip I adopt his score and rule only on the ear's channel.
Sled machining is Panzer's bench; I rule on the sled's *voices*, not its metal.

**Sources read:** `studio/plan/AUDIT-shield.md` in full (R-1…R-13);
`service/TactileSoundEngine.kt` (every voice, every constant, read whole);
`service/SessionSealTransition.kt` (the seal act, the Throw's one call site);
`service/SoundEffectsSetting.kt` (the persisted sound fact, UNA-03);
`service/RoutineHeartbeat.kt` (the routine door into the same transition);
`ui/MonolithViewModel.kt` (every Shield-relevant call site: `setAppBlockTier`,
`cycleAppBlockTier`, `applySmartLoadout`, `triggerRatchetWarning`,
`adjustRoutineTime`, `toggleRoutine`, `cycleRoutineLoadout`,
`toggleRoutineDay`, `addBlockedSite` / `removeBlockedSite`,
`selectCategoryFilter`, `navigateTo`, `toggleSound`);
`ui/views/BlockitSuitcaseAndSwitchesViews.kt` (`AppRowSledToggleView` —
travel tick, commit feels, `commitTo`);
`ui/components/ShieldCapSelector.kt` (the cap's tap sound);
`ui/screens/ShieldMatrixScreen.kt` (the landed issue-7 pass — status item,
grouped catalogue, cap, routines, websites);
`studio/cells/issue-7/feel-haut.md` (the flip choreography I must not
double-speak); plan evidence cited inline (`FLOW-REGISTER`,
`LIE-REGISTER-2`, `CRITICS-PLAN` A-5, `FINISH-LIST-1` F-07,
`PRESENTATION-signal` / `PRESENTATION-dawn` on routine seals).

---

## 0. The law I work under — the seal sound is a claim

One sentence governs everything below, and it is already the app's
shipped law, not my invention:

> **A sound is a claim. The Throw sounds only at proven t=0 — claim the
> row → seal the row → publish → the Throw, last, exactly once. A seal
> that cannot complete never sounds.**

Evidence, so no builder has to take my word for it:

- `SessionSealTransition.executeSeal` is the Throw's **only** production
  call site; `playSealThrow()` is its last statement, after the row
  re-anchor succeeds, after the seal marker commits. A refused `sealRow`
  returns false and no Throw ever fires for that attempt.
- The Throw itself is frozen: 1,000 ms, 44,100 samples, body 360 Hz with
  the shipped 4-partial weights at gain **0.62**, the bolt entering at
  +30 ms at **−6 dB under the body** (peak 0.311), the room at **−12 dB**
  wet, tail crossing −46 dB at 900 ms and fading to exact silence at
  1,000 ms. Golden SHA-256 pinned (`d204b95a…`); any re-voicing turns the
  identity test red. **There is no Shield variant, no night variant, no
  routine variant. Bit-identical for hand arm and routine arm alike.**
- The priming law protects it in the queue: at latch, `primeForSeal()`
  purges pre-latch backlog and drops any legacy command stamped before
  the barrier, so nothing Shield was saying a millisecond earlier can
  delay, stack under, or flam into the Throw.
- The counter-law is just as old and just as binding: voices have been
  *retired* for lying. The ratchet warning once spent the completion
  pulse; Slice E killed that spend in `triggerRatchetWarning` with the
  comment that stands in the tree today — *“the warning is a print; it
  sounds nothing.”* The completion pulse's generic spend died the same
  way in `fireFeel`. This map extends that census to Shield; it does
  not invent a new morality.

From the law, three working rules for this screen:

1. **Nothing on Shield may sound like the seal except the seal.** Any
   voice sharing the Throw's body (360 Hz, gain 0.62, the same partials)
   spends the one recognisable sound in the app on an event that is not
   a sealing. See Finding F-1 — Shield does this today, on its biggest
   control.
2. **One event, one sound, one owner.** The critics' sound law
   (CRITICS-PLAN A-5): the VM speaks for value changes, the view for
   travel. Two layers speaking for one gesture is a flam, and a flam
   is a bug. See Finding F-2 — the sled does this today, on every commit.
3. **Sound after proof, never before it.** The tree already contains
   the correct pattern twice on this very screen's wiring (see §6);
   the incorrect pattern — sound first, outcome later — is how a
   refused flip comes to *sound* successful. See Finding F-2.

---

## 1. The house voice — the only instruments this map may use

No new voices. No new primitives. Everything specified below is an
existing engine call with its shipped constants. Levels are the
engine's mix gains, stated against the Throw's body (0.62 = 0 dB ref);
durations are the transient lengths the engine enqueues.

| Voice (engine call) | Pitch | Duration | Gain | vs Throw body | Character / lawful use |
|---|---|---|---|---|---|
| **The Throw** — `playSealThrow()` | 360 Hz body + bolt + room | 1,000 ms | 0.62 body, bolt −6 dB, room −12 dB | 0 dB (itself) | The seal, at proven t=0, from `SessionSealTransition` only. Haptic twin `[0,24,38,16] @ [0,255,0,175]`. |
| Harmonic seal, legacy — `playHarmonicSeal(arming)` | 360 Hz arm / 480 Hz disarm | 48 ms | 0.62 | 0 dB | The Throw's body with no bolt, no room, no proof. **Banned from Shield entirely (§2, F-1).** Its remaining homes are Deck/valve doors outside this map. |
| Mechanical latch — `playMechanicalLatch(engaged)` | 760 Hz engaged / 520 Hz released | 16 ms | 0.54 | −1.2 dB | Two-stage strike-gap-seat. The house's *commit* voice for a binary rule change: a routine armed/disarmed, a site added/removed, a switch thrown. |
| Firm click — `playFirmClick()` | 620 Hz | 12 ms | 0.50 | −1.9 dB | A heavy press registering: cap travel, a loadout chip cycle. A press, not an outcome. |
| Rotary detent — `playRotaryDetent(hz)` | pitch = the information (see §3) | 6 ms | 0.38 | −4.3 dB | The smallest speaking voice: a stop passed, a value stepped, a filter chosen. The sled's pitch ladder lives here. |
| Seat knock — `playSeatKnock(ms, amp, hz)` | 320 Hz in every Shield use below | 12 ms at the refusal setting | 0.28 at amp 0.2 | −6.9 dB | The *tries-and-stays* knock: the navigation refusal's shipped grammar (`navigateTo`: 6 ms @ 0.2, 320 Hz, throttled 250 ms). Low, short, final. This map adopts it as Shield's one refusal voice. |
| Completion pulse — `playCompletionPulse()` | 185 Hz | 64 ms | 0.68 | **+0.8 dB — louder than the seal body** | Three softening ticks; the finish's reserved voice (E700). **Never on Shield** — F-07's charge stands: a subtask's voice must not out-shout the seal, and nothing on Shield is a finish. |
| Tear pulse — `playTearPulse()` | 240 Hz | 40 ms | 0.68 | +0.8 dB | The counterfoil tear's alone. **Never on Shield.** |

Two properties of the engine shape every ruling below:

- **Haptics are not sound.** `soundEnabled` gates only the PCM path;
  every voice's waveform twin still fires under mute (the engine's S0
  law: *muting loses timbre, never the event*). §5 builds on this.
- **There is no ringer-mode, DND, or night read anywhere in the engine.**
  Playback is `USAGE_ASSISTANCE_SONIFICATION` with transient focus
  (`MAY_DUCK`). The app cannot promise the OS will keep it quiet; its
  one honest mute is its own fact, `SoundEffectsSetting`. §5 says what
  that means at 1 AM and what it must never be used to fake.

---

## 2. Findings — three sound lies on Shield today

### F-1 · The loadout cap spends the seal's voice on a non-seal — the headline

`MonolithViewModel.applySmartLoadout` opens with
`soundEngine.playHarmonicSeal(true)` — 360 Hz, 48 ms, gain 0.62, the
Throw's own body — fired **before** the repository write is even
launched, unconditionally, for an act that seals nothing, starts no
session, and proves nothing. The cap tap stacks a second voice under
it: `ShieldCapSelector` fires `playFirmClick()` on the same tap, so
one gesture currently speaks twice, and the louder of the two is a lie.

This is the exact shape of LIE-REGISTER-2's completion charge (a sound
is a claim; the claim fired before the fact, and the fact's failure
changed nothing about the sound already played) — transplanted to
Shield's most destructive control, the one the audit (R-4) shows
silently rewriting every app's tier plus, for STUDY, the sniper and
the browser blocker. A man who has learned the seal by ear — and the
whole design teaches him to — hears *sealed* when he has only
*reshuffled the rules for next time*. Every hearing makes the real
Throw cheaper.

The same spend sits on three neighbouring VM paths that surface
through Shield's contract family — `addCustomApp`,
`selectAndApplyProfile`, `createCustomProfile`, all
`playHarmonicSeal(true)`. All four die under this map (§3, rows C-2/P-1).

### F-2 · The sled speaks twice per commit — and speaks *success* for a refusal

Trace one sled throw end to end (`AppRowSledToggleView.commitTo` →
`onTierSelected` → `MainActivity` → `VM.setAppBlockTier`):

1. The **view** fires its commit feel immediately, optimistically:
   SHUT → latch-engaged (760 Hz), PEEK → detent 520 Hz, OPEN →
   latch-released (520 Hz).
2. The **VM** then fires its own voice for the same event:
   `playRotaryDetent(freq)` on the tier ladder — SHUT 320 Hz /
   PEEK 560 Hz / OPEN 820 Hz.

Two voices, one gesture, every time. PEEK is the worst: 520 Hz and
560 Hz detents stacked read as one fat, smeared tick — a flam at
nearly the same pitch. SHUT stacks a 16 ms latch under a detent.

The refusal case is worse than the flam. During a session, a
weakening throw (SHUT→PEEK, anything→OPEN) runs step 1 in full — the
ear hears a commit — and is *then* refused by the VM's ratchet check,
which returns without its detent and raises a warning that, correctly,
sounds nothing. Net result at the man's ears: **the flip succeeded.**
Net result at his eyes, per audit R-7: the refusal prints at the top
of the column, off-screen from where he stands. Sound and print
disagree, and the sound is the one that lies. `cycleAppBlockTier`'s
LOCKED→LOCKED tap has the same shape (no view involved there, but the
warning path must stay silent in the VM while gaining the refusal
voice of §4 — see row S-4).

**Ruling (ownership, per the A-5 law):** the view keeps *travel* —
its mid-flight crossing tick (§3, row S-1) is genuinely the view's to
speak. The view's *commit* feels die on the sled path. The VM owns
the outcome, and the outcome has exactly two voices: the tier detent
for an accepted commit, the refusal knock for a refused one (§4).
One gesture will then never carry more than: crossing ticks in
flight + one outcome voice at the seat.

### F-3 · Smaller stacks and unconditional spends (census, for the builder)

- **Cap selector (pre-R-4 state):** firm click (view) + harmonic seal
  (VM) per tap — resolved by F-1's ruling and R-4's Ghost Preview
  split (§3, rows C-1/C-2): preview speaks once, commit speaks once,
  neither is the seal.
- **`removeBlockedSite`** fires `playMechanicalLatch(false)` *before*
  its async remove, unconditionally — the mirror of `addBlockedSite`,
  which correctly sounds only inside `if (repository.addBlockedSite(raw))`.
  Minor (the row exists, so the act's premise is proven by its
  presence), but the ordering law is the ordering law: latch after
  the remove lands, or keep and record as accepted debt. I record it;
  I do not block the merge on it.
- **What is *not* a finding:** `triggerRatchetWarning` sounding
  nothing (correct — print is print, §4 adds the *point-of-refusal*
  knock in the VM's refusal paths, not in the banner mechanism);
  `adjustRoutineTime` returning *before* any sound when the span
  clamp rejects the step (correct — silence on a no-op, §6's model);
  the tally, the status sentence, and search making no sound at all
  today (correct — §3 keeps them silent on purpose, not by omission).

---

## 3. THE SOUND MAP — every Shield event, its voice or its silence

Reading: **Event** is what happened, not what he tapped — where a tap
can end two ways, it gets two rows. **Voice** names the engine call
with its shipped constants; *silence* is a specification, not an
absence of one. Levels per §1.

### A. Blocking itself — the wall going up, standing, coming down

| # | Event | Voice | Reason |
|---|---|---|---|
| A-1 | A session **seals** — hand arm from the Deck, or routine arm through the heartbeat — and blocking goes live at proven t=0 | **The Throw**, once, from `SessionSealTransition` only: 1,000 ms, body 0.62, bolt −6 dB at +30 ms, room −12 dB, haptic twin as shipped | The one claim the whole vocabulary exists to make. Identical whichever door sealed, whichever screen he stands on — including this one. Shield adds nothing to it, ever (HAUT §2 beat 3 concurs). |
| A-2 | Proving **jams** (latch+500, shield re-dispatched) or is **annulled** (latch+1,500, row deleted, window tombstoned) | **Silence** | No seal occurred; any voice here — error buzz, sad latch, consolation click — claims an outcome. The machine retries in silence and the failure speaks in print (FLOW-REGISTER's annulment sentence). A fault speaks in the hand and in print, never in the ear (Signal's law). |
| A-3 | The **status sentence** flips to BLOCKING NOW while he stands on Shield (R-5) — including a routine sealing *while he watches this screen* | **Silence from Shield.** If the flip *is* a seal, A-1's Throw is already this moment's sound; if it is a re-proof, nothing has sealed | A second voice at the sentence would either double the seal or invent a seal. HAUT's SETTLE owns this moment's other channels; the ear's channel is either the Throw or nothing. |
| A-4 | **RUNNING NOW** marker appears on a routine row (R-5) | **Silence** | A derivation re-printing, not an event in the room. The routine's seal already spoke (A-1) at its own t=0, possibly before he opened the app. Markers are read, not heard. |
| A-5 | Blocking **ends** — session expires, window closes, he disarms elsewhere — while he stands on Shield | **Silence from Shield.** The ending's own voices stay with their owners: completion pulse at the finish, harmonic-seal-disarm at the valve/VM doors | Shield is a witness here, not a door. A screen he opened to *check* the wall must not announce the wall's fall at him — and double-speaking doors that already speak is the flam again. (HAUT's RELEASE concurs.) |
| A-6 | A blocked app is **intercepted** somewhere on the phone while Shield is open | **Silence** (on Shield) | The intercept surface owns its own moment (the HELD thud-latch is specced there, deliberately *not* the seal's sound — HPA-17). Per-block sound leaking into Shield would be a running commentary on his behaviour: noise, and a judgement this app does not make. |
| A-7 | Shield is **opened** onto a wall already standing | **Silence** (beyond the nav detent his tab tap already earned, which is navigation's, not Shield's) | No event occurred. Replaying any part of the seal for an audience arriving late is theatre, and daily theatre becomes noise he stops believing (HAUT's NON-EVENT, adopted whole). |

### B. The sled — ruling one app (the catalogue, R-1's grouped rows)

| # | Event | Voice | Reason |
|---|---|---|---|
| B-1 | Rocker **crosses the middle stop** in flight (drag or animated throw, either direction) | Rotary detent, **440 Hz**, 6 ms, 0.38 (−4.3 dB) — the view's shipped crossing tick, kept, once per crossing | Travel, spoken by the view (A-5 law). 440 sits between the ladder's ends: it says *passing*, never *arrived*. Never fires at the seat — arrival is the VM's row below. |
| B-2 | Sled **commits** to **SHUT** (accepted — outside a session, or a tightening inside one) | Rotary detent, **320 Hz**, 6 ms, 0.38, VM-owned, once — the view's commit feel does **not** fire (F-2) | The ladder's low end: the heaviest ruling gets the lowest pitch. Pitch *is* the result, readable eyes-free. The detent — not the latch — because a tier step is a step on a 3-stop scale, and the scale's voice is the detent family end to end. |
| B-3 | Sled **commits** to **PEEK** (accepted) | Rotary detent, **560 Hz**, 6 ms, 0.38, VM-owned, once | The ladder's middle for the middle ruling (the view's old 520 dies with its commit feel; one pitch per fact — 560 is the VM ladder's, and the ladder is one instrument). |
| B-4 | Sled **commits** to **OPEN** (accepted — only possible outside a session) | Rotary detent, **820 Hz**, 6 ms, 0.38, VM-owned, once | The ladder's high end: the wall's lightest ruling sounds lightest. Rising pitch = opening is a ladder he learns once and reads forever, on every row, without looking. |
| B-5 | Sled throw **refused by the ratchet** — a weakening during a live session (SHUT→PEEK, SHUT/PEEK→OPEN) | **Refusal knock**: `playSeatKnock(6, 0.2f, 320f)` — transient 12 ms, gain 0.28 (−6.9 dB, the quietest speaking voice in the house), at the sled, plus the rocker's re-seat. **No detent, no latch** | §4 in full. The current behaviour — view commit-feel, then silence — is F-2's lie. The knock is the shipped *tries-and-stays* grammar: something was attempted, nothing moved. |
| B-6 | Sled throw **cancelled** — finger lifts mid-drag, rocker re-seats on its original stop | **Silence** (beyond any B-1 ticks already honestly travelled) | Nothing was decided. A cancel that sounds like a small commit teaches him the re-seat *did* something. The audit acquits “cancel re-seats” as mechanism; its sound is the absence of B-2…B-4. |
| B-7 | Tap on the **already-seated stop** (no-op throw) | **Silence** | `commitTo` already guards `targetTier != currentTier` before speaking; keep that guard as law for the VM's outcome voices too: a value that did not change has no value-change voice. |
| B-8 | **External re-tier mid-drag** (a loadout lands while his finger is on a rocker — audit R-10/S-3) | **Silence** | The screen must not chirp his hand off the rocker for a change he didn't make. His drag's outcome, when it comes, speaks by B-2…B-5 as usual; the silent-erasure defect is S-3's to fix in state, not in sound. |
| B-9 | Tally **re-strikes** after a commit (250 ms coalesce, whole line) | **Silence** | The tally is a print of the commit the detent already spoke for. A per-re-strike tick would double every sled commit and, under a loadout's burst, machine-gun. Print lags honestly; sound doesn't follow print. |

### C. The cap — the loadout selector (R-4: Ghost Preview, then commit)

| # | Event | Voice | Reason |
|---|---|---|---|
| C-1 | **First tap — Ghost Preview** (hollow caps, ghost tally, consequence line) | Firm click, **620 Hz**, 12 ms, 0.50 (−1.9 dB), once — the cap's existing tap voice, now the *only* voice of this tap | A preview is a press, not an act: the firm click is the house's press-registering voice, and its weight honestly says *something substantial is being considered*. It must not borrow the latch (nothing latched) and must never borrow the seal (F-1). |
| C-2 | **Second tap — commit** (every tier rewritten; STUDY's sniper + browser-blocker side-effects included) | Mechanical latch, **engaged: 760 Hz**, 16 ms, 0.54 (−1.2 dB), **once, after the repository write is underway as one act** — replacing `playHarmonicSeal(true)` outright | A loadout commit is a rule change at scale: the latch is the commit voice for rule changes everywhere else on this screen (routines, sites). One act, one latch — the side-effects get **no** voices of their own; three latches for one decision would claim three decisions. And it is not, under any reading, a seal: no session exists, nothing is proven, the wall has not moved. |
| C-3 | Cap tap **outside** the preview flow (the pre-R-4 single-tap selector, if any surface still fires it) | Firm click only, until the surface gains the preview split | Interim honesty: the press may speak; the harmonic seal may not. |
| C-4 | A **routine's** loadout takes effect at its window (the cap's live-state print changes its source — R-4b) | **Silence** (the session it arms speaks at A-1, once, if it seals) | The cap changing its printed source is a fact re-printing. The *event* is the seal, and it has its voice. |

### D. Routines — arming the future, editing the schedule

| # | Event | Voice | Reason |
|---|---|---|---|
| D-1 | Routine **toggled ON** (the row's cap: ON / ARMED grammar, R-6) | Mechanical latch, **engaged: 760 Hz**, 16 ms, 0.54 — the VM's shipped call, kept | Arming a routine is engaging a mechanism that will act later; the latch says exactly that. It is **not** a seal and must never take the seal's or the harmonic seal's voice — the routine will earn the Throw at its own t=0 (D-5) or not at all. |
| D-2 | Routine **toggled OFF** | Mechanical latch, **released: 520 Hz**, 16 ms, 0.54 — shipped, kept | The latch's other argument: same mechanism, heavier seat, lower pitch. On/off is one instrument played two ways, learnable in one hearing. |
| D-3 | A routine row is found to print **WON'T FIRE** (enabled, daysMask = 0 — R-6) at the moment he toggles it on | The D-1 latch, and **no additional voice** | The latch claims *the toggle engaged* — true. That it will never fire is a printed fact (R-6's word), and inventing a hollow variant latch to whisper it would fork the latch into two dialects for one mechanism. Print carries this one. |
| D-4 | Routine **self-arms**: its window opens, the heartbeat materialises and proves the session | **The Throw** at proven t=0 (A-1's row, through `proveAndSeal` — the same transition, the same buffer, bit-identical) | Shipped law, restated because it is the question this cell was asked: *yes, a routine arm sounds — with the seal, because it **is** a seal.* Dawn's and Signal's filings concur: a self-arm never *adds* a sound the hand-arm doesn't make, and never subtracts one either; the machine does not perform for an empty room, but it does not seal in secret either. The mute/night consequences are §5's, and they are the man's settings to make, not the app's to improvise. |
| D-5 | Routine window **annulled** at proving (tombstoned), or a routine session is **stood down** (SLEEP past window end), or **finalised** by the heartbeat | **Silence** | Machinery concluding in the background. The heartbeat “touches no UI or notification surface” by its own purity law; sound is a surface. An unsealing he didn't perform gets no voice on any screen (see A-5 for the print side). |
| D-6 | Routine row **expands / collapses** | **Silence** — shipped behaviour, kept | Expansion is looking, not deciding. A click here would spend the press voice on navigation inside a row, dozens of times a week. |
| D-7 | Routine **loadout chip cycles** (STUDY→WORK→SLEEP on the expanded row) | Firm click, **620 Hz**, 12 ms, 0.50 — shipped (`cycleRoutineLoadout`), kept | It changes what a *future* session will apply — a setting stepped, not a mechanism engaged: the firm click's weight is the honest size of it. No latch (nothing armed or disarmed), no seal (nothing sealed). |
| D-8 | **Day chip** toggles (M T W T F S S) | Rotary detent, **680 Hz** (the default), 6 ms, 0.38 — shipped, kept | A binary step on a schedule; the detent at its neutral pitch. Seven chips, one voice, no pitch ladder — the days are a set, not a scale, and a ladder would claim an ordering the days don't have. |
| D-9 | **±30 time chips** (the interim editor, until R-8's wheels land) — a step that lands | Rotary detent, **680 Hz**, 6 ms, 0.38 per tap — shipped, kept | Each tap is one value change; the detent is the value-change voice. Thirty identical steps *should* sound identical — the time is read in print, not counted in ticks. |
| D-10 | ±30 step **rejected by the span clamp** (window would leave 30 min…18 h) | **Silence** — shipped ordering (the clamp returns before any sound), kept and cited as law | §6's model case: the function checks, *then* speaks. A rejected step is a no-op, and a no-op has no voice. When the wheels replace the chips, the clamp changes its sound — see E-3 — because a wheel gesture *travelling into* a wall is a different physical fact from a tap that never happened. |

### E. The wheels — R-8's merged editor (W-3: per-detent feel, specced)

The audit merges the routine-edit lane's dual snap wheels and orders
W-3 (per-detent feel) without naming its voice. This is that naming.
One owner only: the critics' doubled-detent charge (wheel view + VM
`setDialMinutes` speaking together) is the standing warning — **the
wheel speaks for travel; the settle speaks once; nothing else speaks.**

| # | Event | Voice | Reason |
|---|---|---|---|
| E-1 | Wheel **detent passed** in travel (minute wheel) | Rotary detent, **680 Hz**, 6 ms, 0.38, per detent, view-owned | The rotary detent is the wheel family's shipped voice (the Deck wheel's grammar). Pitch is constant: minutes are uniform steps, and a pitch ladder over 60 detents would be a melody — pitch-as-formula is dead in this house. |
| E-2 | Wheel **detent passed** (hour wheel) | Rotary detent, **540 Hz**, 6 ms, 0.38, per detent, view-owned | A lower register for the coarser wheel: hour and minute must be distinguishable eyes-free (he will set SLEEP's 20:40 at 1 AM, in the dark, by feel). 540 is in-family (the navigation detent's pitch) — a register shift, not a new voice. |
| E-3 | Wheel runs **into the span clamp** (W-1: the other end must move, and the print names what moved) | **One refusal knock** — `playSeatKnock(6, 0.2f, 320f)`, 0.28 — at the moment travel is first truncated; detents stop for the truncated travel | A wheel physically arriving at a wall is a *tries-and-stays* fact, unlike D-10's tap-that-never-was. One knock per gesture-into-the-wall (re-arm only after travel reverses or the gesture ends — the nav refusal's 250 ms throttle is the floor, not the pattern). The accompanying print (W-1) carries *what* moved; the knock carries only *something resisted*. |
| E-4 | Wheel **settles** (commit-once-per-settle: the time is written) | **One seat knock**: `playSeatKnock(5, 0.4f, 320f)` — gain 0.34 (−5.2 dB) — VM-owned, at the write | The hand-back tick's shipped setting: a small, weighted *seated* — the value has landed and been booked. Quieter than a latch because nothing armed; weightier than a detent because something finished. Never per-detent: settle is once per gesture, by the lane's own commit law. |
| E-5 | Wheel gesture **abandoned** (no settle, editor dismissed unchanged) | **Silence** | As B-6: an undecided gesture has no outcome voice. The detents it travelled were honest travel; there is nothing to add. |

### F. Websites, search, filters, and the screen's furniture

| # | Event | Voice | Reason |
|---|---|---|---|
| F-1 | Site **added** (repository accepts) | Mechanical latch, **engaged: 760 Hz**, 16 ms, 0.54 — shipped conditional call, kept | A rule engaged. The shipped code sounds *only inside* the success branch — §6's second model case. |
| F-2 | Site add **rejected** (R-3: the field keeps its text and prints why) | **One refusal knock** at the field — `playSeatKnock(6, 0.2f, 320f)`, 0.28 | The rejection is an outcome at a control, so it earns the refusal voice — the same knock as B-5/E-3: one refusal dialect for the whole screen, learned once. Silence would leave the kept text ambiguous (did it add?); a latch would lie. |
| F-3 | Site **removed** | Mechanical latch, **released: 520 Hz**, 16 ms, 0.54 — shipped, kept (ordering debt recorded at F-3§2) | A rule disengaged: the latch's released seat, symmetric with F-1. |
| F-4 | **Typing** in search or the site field; caret moves; text clears | **Silence** | Keystrokes are not the app's events. No surface in this app sounds typing; Shield will not start. |
| F-5 | **Category filter word** tapped (ALL / category) | Rotary detent, **580 Hz**, 6 ms, 0.38 — shipped (`selectCategoryFilter`), kept, once | A view stepped to a neighbouring state: the detent at a neutral mid pitch (it is neither a ruling ladder step nor travel on a wheel — 580 claims nothing but *the list changed hands*). The *results* (rows re-dealing) are silent: they are print following a spoken cause. |
| F-6 | **Tally words tapped** — the R-1 jump (tap SHUT/PEEK/OPEN, the list scrolls to that group) | Rotary detent, **540 Hz**, 6 ms, 0.38, once per tap | In-page travel, spoken once at departure — navigation's detent register (540 is `navigateTo`'s pitch). Never per group scrolled past: the scroll is print moving under a spoken cause, as F-5. Landing adds nothing; the group header arriving is seen, not heard. |
| F-7 | **Group header / section header** appears, catalogue re-groups after a commit (a row migrates PEEK→SHUT) | **Silence** | Re-grouping is the tally's and the commit's print shadow (B-9's law). If every migration chirped, one loadout commit would chirp ~74 times. |
| F-8 | **Reset banner / prune note** appears or is dismissed; **WON'T FIRE / PEEK contract line** prints (R-6) | **Silence** | One-time print furniture. These lines inform; they decide nothing and conclude nothing. A voice here would be the app clearing its throat. |
| F-9 | **Search finds nothing** (“NO APP BY THAT NAME…”) | **Silence** | An absence printed as a fact (the audit acquits the line). Absences don't sound. |

### G. Refusals beyond the sled — one dialect, everywhere

| # | Event | Voice | Reason |
|---|---|---|---|
| G-1 | Ratchet refuses a **daily-limit loosening** (VM warning path) | **Refusal knock** at the control — `playSeatKnock(6, 0.2f, 320f)`, 0.28, once | Same law as B-5: an attempted weakening, refused, at the point of attempt. The banner (R-7: reworded, tokenised, moved in place) is the print half; the knock is the ear's half. |
| G-2 | Ratchet refuses **disabling an active schedule/profile** | **Refusal knock**, as G-1 | One dialect. He should never need to ask *which* refusal he heard. |
| G-3 | The **ratchet banner itself** appears / expires (3.6 s) | **Silence** — the shipped Slice E ruling, kept verbatim in spirit | The banner is print *about* a refusal whose knock already spoke at its control (G-1/G-2/B-5). Sounding the banner too would double every refusal — and sounding it for Deck-origin refusals (where no Shield control was touched) would invent a location. Banner: print only, forever. |
| G-4 | **Navigation refused** while sealed (he taps another tab mid-session) | The shipped knock: `playSeatKnock(6, 0.2f, 320f)`, 250 ms-throttled — **unchanged; it is this map's source, not its subject** | Cited so the builder sees B-5/E-3/F-2/G-1 are *adoptions of an existing grammar*, not a new invention: the house already had a refusal voice; Shield simply wasn't using it where Shield refuses. |

### H. Explicitly voiceless — the banned list for this screen

For the avoidance of every future doubt, on Shield **never** sound:

- `playHarmonicSeal` in either direction, for any event (F-1).
- `playCompletionPulse` (the finish's; louder than the seal — F-07).
- `playTearPulse` (the counterfoil's).
- Any per-row, per-site, or per-app voice during a **loadout commit's
  cascade** — one latch (C-2) speaks for the whole act.
- Any sound for a **re-proof, a marker, a tally, a re-grouping, a
  banner, a note, or an absence** (rows A-3…A-5, B-9, F-7…F-9, G-3).
- Any **ambient bed, loop, or armed-state hum** under BLOCKING NOW.
  The engine's charter already forbids ambient loops; a standing wall
  is silent. Stillness is the sound of it standing.
- Any **TalkBack earcon of the app's own** duplicating R-11's spoken
  semantics. The system's speech is the aural print channel (HAUT §7);
  the app adds no jingles over it.

---

## 4. The refusal at the sled — the full specification (B-5, expanded)

This is the moment the task named, so it gets its own section.

**Today:** throw weakens → view plays the commit voice → VM refuses
silently → banner prints at the top of the column, off-screen. Ear:
success. Eyes (if he scrolls): refusal. Hands: the rocker *does*
re-seat (the tier never changed, so the row re-renders at its true
stop) — three channels, two stories.

**Specified:**

1. **The view stops pre-speaking.** On the Shield sled path,
   `fireCommitFeel` is not called at commit. The view's only voice is
   B-1's crossing tick, which is true regardless of outcome — he *did*
   travel.
2. **The VM speaks the outcome, once, at the seat:**
   accepted → the tier detent (B-2…B-4); refused → the refusal knock
   `playSeatKnock(6, 0.2f, 320f)`.
3. **Why this knock and no other.** It is (a) *already the house's
   refusal* — the sealed-navigation refusal ships with exactly these
   constants and the comment “tries and stays”; (b) *maximally
   distinct from every commit voice* — shortest, quietest (−6.9 dB
   under the seal, −2.6 dB under even the detent), lowest-pitched of
   the transient family at 320 Hz with the seat-knock's weighted
   (not bright) timbre, so it cannot be misheard as the SHUT detent
   that shares its pitch: the detent is 6 ms at 0.38 with the rotary
   waveform `[0,7,5] @ [0,180,70]`; the knock is a 6 ms strike at
   amplitude 51/255 with the seat waveform — same frequency, different
   *object*, like a knuckle on the sled's housing versus a tooth
   engaging; (c) *haptic-paired* — its waveform twin fires with it,
   so the refusal is felt at the sled his finger is still on, which
   is R-7's “at the point of refusal” satisfied in two channels.
4. **Throttle:** repeated throws against the ratchet re-knock per
   throw (each throw is a fresh attempt), but never stack — the
   nav refusal's 250 ms floor applies: throws faster than that merge
   into the standing knock + banner refresh, they do not accumulate
   a drumming. A man rattling the sled hears *a wall*, not a rattle.
5. **The banner stays silent** (G-3) and stays as R-7 rewords/moves
   it. Knock + in-place print is the whole refusal; there is no
   third element.

## 5. Mute and night — the two rooms this screen is used in

### 5.1 Sound Effects OFF (the app's own fact)

`SoundEffectsSetting` is a persisted fact, pushed to every live
engine on write and re-read at every bind (UNA-03's three laws) — so
OFF means OFF on *every* row of §3, including the heartbeat-born
engine that seals routines. What remains is the haptic twin of each
voice (§1): under mute, the map reads as a touch map — detents become
ticks in the finger, the latch a two-stage tap, the refusal knock a
single low tap, and **the Throw becomes haptic-only** (`playSealThrow`
with sound off still fires `[0,24,38,16] @ [0,255,0,175]` and purges
the queue; it loses timbre, never the event). No row of §3 may
distinguish itself under mute by *adding* a haptic it doesn't have
when sound is on — the twins are the voices' own, not a shadow
vocabulary. Toggling the setting itself: the shipped grammar stands
(a 720 Hz detent sounds only when turning ON — confirmation by
demonstration; turning OFF is silent, because the first silent thing
should be the confirmation).

### 5.2 The device is silenced / in DND

As inspected: the engine reads **no** ringer mode and requests only
transient focus. I therefore spec no behaviour the code cannot
promise: the app's contract is its own toggle (5.1), full stop.
Anything Android's sonification routing does with a silenced device
is the OS's conduct, not a Monolith voice, and no Shield surface may
print or imply otherwise. Flagged to VOGT as a standing limitation,
not a merge item: if Sarthak ever wants a guaranteed-silent night,
5.1 is the honest switch, and it already exists (PARK's toggle, the
UNA-03 fact).

### 5.3 Night — the sealed palette, the 1 AM room, the sleeping house

- **No night variant of any voice.** Shield at night is the same
  instrument (`effectiveNight = darkTheme || sealed` changes light,
  never sound). A detent that drops pitch after dark is a second
  dialect; a Throw ducked for night is a second seal — bit-identity
  forbids the one, learnability forbids the others. His ear calibrates
  once, for all hours.
- **Hand-made sounds at 1 AM are his to make.** Every §3 voice except
  A-1/D-4 is the direct consequence of his own finger on a control,
  at gains between −6.9 and −1.2 dB under the seal. A man setting
  SLEEP 20:00→20:40 at 1 AM hears small mechanical sounds he is
  causing, at conversational-instrument level. Nothing in this map
  sounds *at* him uninvited while he works this screen — no chime
  when a routine's window opens under his hands (A-4), no alert when
  proof slips (HAUT's SLIP: print only).
- **The one uninvited voice is the seal itself** (D-4): a SLEEP
  routine sealing at its hour speaks the Throw in a quiet house,
  possibly to an empty room, possibly to a sleeping man. That is the
  shipped law's deliberate price — a seal that whispers is a seal
  that can be doubted, and this app's whole contract is that the
  seal is believed. The remedy is his, and it is honest: Sound
  Effects OFF before bed leaves the haptic twin on the nightstand
  and takes the timbre away. What the app must never do is decide
  for him — no automatic night-duck, no “routine seals are quieter”
  footnote. One seal, one voice, his switch.

## 6. The ordering law — sound after proof (builder's rule, with the tree's own models)

Every change this map asks for is an *ordering* change before it is
a voice change. The rule, stated once for the merge:

> **Check, then speak. Persist or refuse, then speak. Never speak on
> the gesture and correct with the outcome.**

The tree already obeys it in two Shield places — copy them:

- `adjustRoutineTime`: computes the candidate, tests the span,
  **returns in silence if invalid**, only then detents and persists.
- `addBlockedSite`: the latch sits *inside*
  `if (repository.addBlockedSite(raw))`.

And disobeys it in the two places this map fixes:

- `applySmartLoadout`: harmonic seal *first*, async write after,
  result unexamined → C-2's latch rides the commit call, and if a
  future repository makes the write fallible, the latch moves inside
  its success with it.
- The sled path: view speaks at the gesture, VM adjudicates after →
  §4's split (travel in the view, outcome in the VM).

A corollary for the seal's shadow: Shield sounds enqueued in the
same instant a seal primes may be dropped by the seal lane's barrier
purge. That is correct and must not be “fixed”: a detent swallowed
by an arriving Throw is a voice yielding to a greater claim, exactly
as the queue law intends. It never stacks, never delays, never flams.

## 7. What this costs / builder notes (DECLARED — no code by KLANG)

Small. No engine changes, no new voices, no schema, no constants
touched — every voice above is a shipped call at shipped settings.
The work is call-site surgery in three files the merge already opens:

1. `MonolithViewModel.kt` — `applySmartLoadout` (and the three
   sibling profile/custom-app paths): `playHarmonicSeal(true)` →
   `playMechanicalLatch(true)` at the commit (F-1/C-2). The ratchet
   refusal returns in `setAppBlockTier` / `cycleAppBlockTier` and the
   two other warning paths: add the one refusal knock at the refusal
   return (B-5/G-1/G-2) — one shared private helper, three call
   sites, so the dialect cannot drift. `removeBlockedSite` ordering
   (F-3) optional in this merge.
2. `BlockitSuitcaseAndSwitchesViews.kt` — `AppRowSledToggleView`:
   retire `fireCommitFeel` on the commit path; keep `noteTravel`'s
   440 Hz crossing tick exactly as shipped (B-1).
3. `ShieldCapSelector.kt` — unchanged while the single-tap selector
   stands (its firm click is already C-3's voice); when R-4's Ghost
   Preview lands, the preview tap keeps the click and the commit tap
   must *not* re-fire it — the latch (item 1) is the commit's only
   voice.
4. The wheels (R-8 merge, when MONTEUR lands them): wire E-1…E-4 at
   the lane's existing detent/settle hooks; one owner per channel
   per §E's preamble.

**Verification (device, ears, at Ren's build):** (1) sled each tier
both directions, in and out of session — one voice per commit, pitch
ladder readable eyes-closed; (2) weaken against the ratchet — knock
at the sled, rocker re-seats, no commit voice anywhere in the throw;
(3) tap the cap through preview→commit — click, then latch, and at no
point anything resembling the seal; stand on Shield and arm from the
Deck — the Throw, unmistakable and unaccompanied, proves the
contrast; (4) routine SLEEP window opens with Shield open and with
the phone pocketed — one Throw, identical; annul a proving (kill the
shield service pre-t=0) — silence; (5) Sound Effects OFF — repeat
(1)–(4) as pure touch: every event still distinguishable in the
finger, nothing audible; (6) wheels — detents uniform, hour/minute
registers distinct, clamp knocks once, settle knocks once.

## 8. What I want next / unassigned

- The Deck's arm cascade ends where A-1 begins; a joint listening
  pass with the Deck's feel owner over the latch→prime→Throw window
  (what the last Shield detent *before* a seal should do when the
  barrier purge takes it — §6's corollary — deserves one heard
  confirmation, not just a lawful one).
- PARK inherited the sniper/blocker sleds (R-9); their voice today is
  the binary `SledSwitchView`'s firm click at throw — a button in
  sled costume in sound as in dress (Vega S-4). Offered, unassigned:
  the same map treatment for PARK — latch-outcome grammar — so the
  two screens that share the sled metal share its language.
- If F-07's hierarchy re-voicing (finish list) ever lands — seal
  body up, completion down — every relative level in §1 re-derives
  mechanically from the new constants; the *map* (which voice, which
  silence) does not change. I ask to be re-seated for the half-hour
  re-derivation rather than let the numbers drift stale.

— KLANG, FEEL SPECIALIST 2 (SOUND), VOGT's issue #7 cell. DECLARED, not built.

---

## Appendix — draft GitHub comment for issue #7 (for VOGT to post)

— KLANG (FEEL · SOUND), for VOGT's issue #7 cell.

The Shield sound map is filed at `studio/cells/issue-7/feel-klang.md`
(DECLARED — design only, no code, nothing compiled). It maps every
Shield event to an existing engine voice at shipped constants — or to
a specified silence — under the one law that governs the app's ear:
**a sound is a claim, and the seal Throw sounds only at proven t=0,
bit-identical, from `SessionSealTransition` alone.** Three findings
ride with the map:

1. **The loadout cap spends the seal's voice on a non-seal.** 
`applySmartLoadout` fires `playHarmonicSeal(true)` — 360 Hz, 48 ms,
gain 0.62, the Throw's own body — before its write even launches,
for an act that seals nothing (the same spend sits on the custom-app
and profile paths). Under R-4's Ghost Preview split the preview tap
keeps the cap's firm click (620 Hz, 12 ms, 0.50) and the commit
takes one mechanical latch (760 Hz, 16 ms, 0.54) for the whole act,
side-effects included. The harmonic seal is banned from Shield
entirely.
2. **The sled speaks twice per commit — and speaks success for a
refusal.** The view fires a commit feel optimistically, then the VM
fires its tier detent for the same gesture (PEEK stacks 520 Hz under
560 Hz). Worse: on a ratchet-refused weakening the view's commit
voice is the *only* sound — the ear hears success while R-7's banner
prints elsewhere. Fix by ownership (VM speaks for outcomes, view for
travel): the view keeps its 440 Hz crossing tick and loses its
commit feels; accepted commits take the VM's pitch ladder alone
(SHUT 320 / PEEK 560 / OPEN 820 Hz, 6 ms detents at 0.38).
3. **Shield needs — and already owns — a refusal voice.** The
shipped sealed-navigation refusal, `playSeatKnock(6, 0.2f, 320f)`
(12 ms, gain 0.28, the quietest voice in the house), is adopted as
the one refusal dialect at the point of refusal: sled weakenings,
the wheels' span clamp, rejected site adds, and the other ratchet
refusals all knock identically, haptic-paired, 250 ms-throttled.
The banner itself stays silent, per Slice E's standing ruling.

The map's other half is specified silence: the status sentence and
RUNNING NOW markers never sound (a routine sealing while he watches
is spoken by the Throw itself, once — A-1); jams and annulments are
silent; loadout cascades, tally re-strikes, re-groupings, banners
and absences are silent; the completion and tear pulses never appear
on this screen. R-8's wheels get their W-3 naming: detents per wheel
(minute 680 Hz / hour 540 Hz, view-owned), one clamp knock, one
settle knock (5 ms @ 0.4) at the commit-once-per-settle write.

Mute and night are ruled, not improvised: Sound Effects OFF turns
the whole map into its haptic twins — the Throw included (S0:
muting loses timbre, never the event); the engine reads no ringer
or DND state, so the app's toggle is the only honest mute and no
surface may imply more; and there is **no night variant of any
voice** — a routine seal at its hour sounds the identical Throw in
a quiet house, because a seal that whispers is a seal that can be
doubted, and the remedy (his toggle) is his to make. Cost: call-site
surgery only, no engine or constant changes, with six device
listening checks listed in the spec.

— KLANG
