# Issue #10 — Receipt (audit fixes) · FEEL SPECIALIST 4 (sound)

**Seat:** Feel Specialist 4 (sound), Zettel's cell for GitHub issue #10,
"Receipt audit fixes".
**Source of truth:** `studio/plan/AUDIT-receipt.md` (Zettel, ruled Oct 30
01:15 IST; read in the `wt-ci` clone,
`~/workspace/clone-inspect/builds/wt-ci/studio/plan/AUDIT-receipt.md`) —
items 14 (pulse ordering), 18 (arrival is a FEED), 21 (silences), and the
binding acquittals.
**Code read:** this cell's clone
(`~/workspace/clone-inspect/builds/cells/issue-10/`, branch
`agent/issue-10`). All line citations below are to that tree.
**Sibling spec composed with:** `work/feel-1-arrival.md` — the feed is
280 ms / Curve A / 56 dp, starting at issuance, identical on the faulted
branch, never performed by a restored slip. This document adds no motion
and moves no issuance/detention timing.
**Standing constraint honoured:** nothing here compiles, builds, or tests
— paper only. Nobody messages Sarthak.

---

## 0. The one idea the whole map hangs on

**The machine sounds; the paper doesn't.**

Every sound in the receipt lifecycle today is a sound of the *shield
stopping* — the session's machinery winding down. The slip itself is a
printed object that arrives after that proof. Paper being fed, paper
landing, paper being taken away, paper already standing in the room:
none of that is a machine act, and none of it gets a voice. This is not
an aesthetic preference; it is the same truth law the audit applies to
print. A sound, like a printed line, is a claim. The lifecycle may only
sound claims the machine has *already proven* at the moment the sound
fires — which is exactly where the current code breaks (§2).

Three corollaries, used throughout:

- **The completion pulse is a reserved verdict, not a reward jingle.**
  `playCompletionPulse()` means exactly one thing — *a verified,
  fully-elapsed, clean finish whose shield is confirmed stopped* — and
  it is spent once per session, ever, or not at all. No variant, no
  quieter version, no "partial credit" pitch-shift of it exists.
- **Silence is a verdict, not an absence.** Several rows of the map are
  silence *by ruling* (GRACE_CANCEL binding, audit item 21; the E700
  slot standing EMPTY on death-booked finishes, acquitted). An empty
  slot must be produced by *not calling anything* — no acoustic transient
  **and no haptic twin**. In this engine the two ride the same call site
  (every voice below fires its waveform haptic and its PCM transient
  together), so "silent" in this map always means: the call never
  happens, in either channel.
- **The sound gate is inherited, never re-decided.** Every call below
  passes through the engine's existing gate
  (`TactileSoundEngine.kt:291`, `soundEnabled`, born from the persisted
  `SoundEffectsSetting` fact). With sound effects OFF, the acoustic half
  drops and the haptic twin still lands — muting loses timbre, never the
  event. With the call withheld (fault, PARTIAL, dismissal…), *both*
  halves drop. The ordering cure in §4 governs the **call**, so it can
  never split the twins: a faulted finish must not vibrate the clean
  pulse while staying acoustically quiet.

**Scope:** this map governs the finish/receipt sound lifecycle only —
the E-clock (`onFinishE0`) and the slip's own events. Sounds belonging
to the acts that *end* a session by hand (the disarm seal, latches,
detents elsewhere in `MonolithViewModel.kt`) are those acts' voices,
not receipt sounds, and are not re-specced here.

---

## 1. The code as it stands — evidence

### 1.1 The E-clock's sounds (the only receipt sounds that exist)

`ui/MonolithViewModel.kt`, `onFinishE0`, **L1241–1262**. One detached
`launch{}` off `e0Mono` fires, against the clock alone:

| Slot | Code | Voice |
|---|---|---|
| E0 | L1248 `playSeatKnock(24, 1.0f, 320f)` | weighted seat knock |
| E700 | L1250–1254: `delay` to 700 ms, then if elapsed < 900 ms, `playCompletionPulse()` — only if `pulses` | completion pulse |
| E880 pair | L1256–1260: `delay` to 880 ms, `playSeatKnock(24, 1.0f, 320f)`, +90 ms `playSeatKnock(12, 0.5f, 320f)` | power-down knock pair |

Eligibility is decided once, at E0, by
`com.example.data.finishPulses(kind, row)` (L1246).

### 1.2 The eligibility gate (correct, and not the problem)

`data/ReceiptState.kt:108–117`, `finishPulses`: MANUAL_FINISH pulses
iff `outcome != ELAPSED_SHIELD_GAP` (death-booked never pulses, B-12);
FLOW_FINISH, ROUTINE_ATTENDED, ROUTINE_SLEEP_STANDDOWN pulse;
ROUTINE_PARTIAL and everything else never do. The acquittals are
already honoured *here*. GRACE_CANCEL, VALVE_BROKE and ENDED_EARLY
never reach the clock at all: `finishKindOf` (`ReceiptState.kt:69–105`)
returns null for them, so no E0 exists for them — their silence is
structural, not a branch.

### 1.3 The voices

- `playSeatKnock(durationMs, amplitude01, pitchHz)` —
  `service/TactileSoundEngine.kt:665`. Weighted knock; haptic strike
  `durationMs` + half-length tail, transient at `pitchHz`, gain
  `0.22 + 0.3·amplitude`.
- `playCompletionPulse()` — `TactileSoundEngine.kt:833–846`. 185 Hz,
  64 ms, gain 0.68, chord 1.33; haptic `[0,10,24,10,24,14]` ms @
  `[175,0,125,0,80]` — "three ticks that soften as they land — the
  machine winding down, not alarming."
- `playTearPulse()` — `TactileSoundEngine.kt:817–831`. Exists, is the
  counterfoil tear's voice (live use: `FocusSequencerScreen.kt:153`),
  and is **not** spent anywhere in this map (§5, dismissal).

### 1.4 The events that currently make no sound at all

- **The slip's composition/feed:** `ReceiptSheet.kt:60–65` is a pure
  `AnimatedVisibility` fade (fade itself killed by item 18 / A7); no
  sound call is attached to it or to `emitReceipt`
  (`MonolithViewModel.kt:1321–1345`).
- **Dismissal:** `MainActivity.kt:783` — `onDismiss` only sets the
  remembered `dismissedReceiptSessionId`; the scrim's `clickable`
  (`ReceiptSheet.kt:66`) carries no sound/haptic.
- **Restoration:** `runSliceERecovery` (`MonolithViewModel.kt:1098–1103`)
  assigns `rehydrateReceipt()`'s result directly to `_receiptState`,
  bypassing `emitReceipt` and the whole E-clock. No sound, by
  construction.

These three silences are, this seat rules, **correct as shipped** —
§3 defends each rather than "fixing" them.

---

## 2. The fault — item 14's pulse-ordering lie, stated precisely

The fault verdict does not exist at E700. It is computed in the
*detention loop*, a **separate coroutine** from the sound `launch{}`:

- `Detention.FAULT_DEADLINE_MS = 1520` (`ReceiptState.kt:206`);
  `Detention.isFaulted(pawlLiftMs, halted, nowOffsetMs)`
  (`ReceiptState.kt:219–220`) is true iff the chain halted, or no
  pawl-lift **P** (the frame `MonolithShieldService.instance` reads
  null — the one commanded link, the shield's stop, confirmed open)
  has been observed by E1520.
- The detention loop (`MonolithViewModel.kt:1264–1317`) observes P,
  computes the verdict, publishes with `faulted = …`, and issues the
  faulted slip at E1800 exactly.

The sound job knows none of this. It reads `finishPulses(kind, row)` —
a fact about the *session's outcome* — and fires at E700 on a clean-
eligible kind (e.g. a manual ELAPSED_CLEAN) **even when the shield then
fails to stop**, the verdict lands faulted at E1520, and the slip
prints `SHIELD — STILL BLOCKED`. The ear has already been told the
clean-finish verdict 800+ ms before the machine knew it was false,
and nothing ever retracts it. A sound cannot be unsounded; the only
cure is to never fire it on credit.

Two secondary defects in the same block, recorded for the cure:

- **The <900 ms guard (L1252)** silently *drops* a lawful pulse if the
  delay overshoots — eligibility decided by thread scheduling, in the
  opposite direction: a clean finish can lose its pulse for no fact.
- **The E880 pair has the same ordering exposure, one rank down.** Its
  filed meaning is the power-down pair — the shield's stop landing.
  Fired unconditionally at E880, it too can assert a stop that the
  E1520 verdict then denies. §4 flags this as this seat's extension
  of item 14 (the audit names only the pulse); in the normal case
  (P ≤ E700) the cure changes **no timing at all**.

---

## 3. THE SOUND MAP — receipt lifecycle, event by event

All offsets from E0 (the finish's zero, `onFinishE0`) unless stated.
"Knock" = `playSeatKnock(24, 1.0f, 320f)`; "pair" = knock, +90 ms
`playSeatKnock(12, 0.5f, 320f)`; "pulse" = `playCompletionPulse()`,
unmodified, exactly once. Timings for pulse/pair below are stated
**as cured by §4** (gate first, clock second).

| # | Event | Sound | When |
|---|---|---|---|
| 1 | **E0 — the finish registers** (witnessed path only) | **Knock** | E0, unconditional |
| 2 | **Clean finish** — MANUAL_FINISH clean / FLOW_FINISH / ROUTINE_ATTENDED / SLEEP stand-down, stop confirmed | **Pulse**, then **pair** | Pulse at max(E700, P); pair 180 ms after pulse |
| 3 | **Faulted finish** (no P by E1520, or chain halted) | **Silence after the E0 knock — forever** | Pulse slot and pair slot both stand empty; nothing at issuance (E1800), nothing on any later retry |
| 4 | **PARTIAL finish** (ROUTINE_PARTIAL) | **Knock + pair; pulse slot EMPTY** | Knock at E0; pair at max(E880, P) (no pulse precedes it); if the stop itself faults, row 3 governs |
| 5 | **Death-booked finish** (MANUAL_FINISH ∧ ELAPSED_SHIELD_GAP) | **Knock + pair; pulse slot EMPTY** | Identical to row 4 (B-12 acquittal, carried) |
| 6 | **The FEED** (issuance; 280 ms slide, Feel-1) | **SILENCE** — no acoustic, no haptic | Whole 280 ms, every branch, faulted included |
| 7 | **Settle** (feed's last frame; slip at rest) | **SILENCE** | No landing knock, no seat tick |
| 8 | **Dismissal** (DOWN/scrim, navigation, PARK, next arm — and the faulted slip's explicit act) | **SILENCE** | At the dismissal event, every route |
| 9 | **Restored slip** (rehydration at process birth) | **SILENCE — total** | No catch-up pulse, no knock, no feed sound, ever |
| 10 | **Branch-U late discovery** (finish found 1.8 s–10 min old; claim + issue at discovery) | **SILENCE — total** | Already the code's behaviour (no E-clock runs); stated so it cannot drift |
| 11 | **GRACE_CANCEL** | **SILENCE — binding** | No finish kind exists; no E0, no slip, no sound (audit item 21, acquitted) |
| 12 | **VALVE_BROKE / ENDED_EARLY / MISSED** | **SILENCE** (receipt lifecycle) | No finish kind, no slip; the valve/disarm acts keep their own upstream voices, which are not receipt sounds (§0 scope) |

### Reasoning, per row

**Row 1 — E0 knock, unconditional.** The knock's claim is only *"the
span has ended and the machine has registered it"* — true at E0 on
every witnessed finish, faulted ones included, because the detention
loop and the claim both start from this frame. It is the machine
beginning to stop, not the machine declaring it *has* stopped. Keeping
it unconditional is what makes rows 3–5 legible: the listener hears
the register, then hears the verdict slot either fill (pulse) or stand
empty. Remove it from faulted finishes and the fault becomes
indistinguishable from "nothing happened."

**Row 2 — clean finish: pulse, then pair, in that order.** This is the
shipped Wren cadence (pulse inside the held stillness, pair as the
power-down after it) preserved exactly in the normal case: P is
normally observed within a few hundred ms, so max(E700, P) = E700 and
pair = E880. The pulse's claim — verified finish, shield stopped — is
only uttered once both halves are facts (§4).

**Row 3 — faulted finish: the empty slots are the sound.** A faulted
finish gets no substitute voice: no error buzz, no dropped-pitch pulse,
no knock of sympathy. Inventing a "fault sound" was considered and
rejected — (a) the slip already carries the fault in print, in the
machine's boldest line; the ear does not need a second, vaguer dialect
of it; (b) any new timbre spent here becomes a sound a tired man hears
at 1 AM and must decode, where silence-after-the-register-knock
decodes itself: *the machine started to stop, and the completion never
came*; (c) the engine has no fault voice today, and minting one is new
machinery priced well beyond item 14's VM-side ordering cure. The
silence is also **permanent for that session**: the release-retry
cadence (`ReleaseRetryScheduler`, scheduled at faultWall + 30 000 in
the detention loop) is a service act, not a finish — when a retry later
succeeds, it fires **no** pulse, no pair, no knock. A pulse arriving
30 s after its slip would re-book a faulted finish as clean in the
only channel that cannot show its working.

**Rows 4–5 — PARTIAL and death-booked: knocks yes, pulse never.** The
distinction the map must hold: the knocks voice *machine acts* (the
register, the shield physically stopping — both real on these
finishes), the pulse voices a *verdict* (verified full completion —
false on both). A PARTIAL routine window ended and its shield stopped;
silencing the pair would lie about the machine to protect a grade.
But the E700 slot stands empty, with no consolation variant: a
softer/quieter pulse for PARTIAL was considered and rejected — it
would spend the reserved voice at lower gain, and gain is not a
grammar; he would hear "completion, but less," which grades the
session in a dialect the slip itself refuses (the slip prints PARTIAL
only with its evidence, item 7/B5, or not at all). Death-booked is the
same shape by B-12's standing acquittal: the session elapsed fully but
verified nothing while the shield was gone, so the verdict voice is
never spent on it.

**Row 6 — the FEED has no sound. This is the row the brief asked
about; the answer is no, deliberately.** Four grounds:

1. *Attribution.* Any sound at issuance (E1480–E1800, or E1800 faulted)
   arrives 700–1000 ms after the E-clock's last event and would be
   heard as the *slip's* sound — the first time in the lifecycle a
   sound claims to come from the paper. The paper claims nothing; that
   is its character (zero buttons, zero CTA, no re-strike).
2. *No lawful primitive exists.* The engine synthesises sine-partial
   transients; it has no paper/feed noise voice. The only noise-
   adjacent voice in the building is the tear (`playTearPulse`,
   §1.3), which is *reserved* — it means separation, its one live use
   is the counterfoil, and the audit's TEAR proposal (B1) is parked
   awaiting Ren's ruling. Spending it on every feed would pre-spend
   B1 and teach the ear that feeding = tearing. A new synthesised
   "whirr" is new engine machinery: unpriced, unasked, and — as a
   looped/ramped noise under a 280 ms slide — the one sound in this
   map that would be *imitating* a mechanism Monolith does not have.
   The machine does not have a paper motor; it must not sound like
   one.
3. *The wait was already paid in sound.* The feed is not the event;
   it is the *result* arriving (Feel-1 §3: the detention paid for the
   arrival). The lifecycle's acoustic story — register, verdict,
   power-down — has fully concluded by E880 (normal case) before the
   first feed frame. Silence after the pair is precisely how the ear
   knows the machine is done and what follows is just paper.
4. *The faulted branch must feed identically* (Feel-1 check 3) — a
   feed sound would either falsely celebrate a faulted slip or force
   a sad variant, both rejected at row 3.

*Rejected alternative, recorded:* a single lowest-weight seat-knock at
the feed's landing frame ("the paper seats"). Rejected: it re-strikes
after arrival, which the acquittals forbid in spirit ("stillness after
arrival: no re-strike"), it double-books the landing the pair already
voiced, and on the faulted branch it would knock for a shield that
never stopped.

**Row 7 — settle is silence.** Naming note: the code's own comment
calls the E0 knock the "settle knock" (L1243) — that settle is the
*machine* settling at E0 and is row 1. The *slip's* settle — the last
frame of the 280 ms feed, paper coming to rest against its anchor —
gets nothing: no landing tick, no soft thock. EasingC's successor
Curve A already lands without overshoot; a sound at the landing would
be a second full stop after the pair's, punctuation the sentence
doesn't need. After settle: the standing stillness the acquittals
protect — no idle sound, no re-strike, for as long as the slip stands.

**Row 8 — dismissal is silence, on every route.** Dismissal is *his*
hand taking the paper away, not a machine act; the machine does not
comment on being filed. Currently true by construction (§1.4) and
ruled to stay true when A8 lands (DOWN gesture + 2 s dwell + faulted
explicit act): the gesture work must not accrete a click. Two specific
prohibitions: (a) **not the tear** — `playTearPulse()` stays unspent
here even though dismissal is "the tear's" conceptual slot, because
B1 (TEAR as the closing gesture that *files* the slip) is unruled;
until Ren rules, a dismissal persists nothing (interim A9 persists
only the №), and sounding a tear for an act that files nothing would
be the W-41 pattern in audio — a claim ahead of its fact. If B1 is
ruled in later, the tear voice is spent *then*, by that cure, not
pre-spent now. (b) **the faulted slip's explicit dismissal act is
equally silent** — its weight comes from the deliberate gesture A8
requires, never from a heavier sound; a special "acknowledged" thock
would let the sound do the acknowledging, which is the act's job.

**Row 9 — restored slip: total silence, including no catch-up.** A
restored slip's finish moment is gone; its E700 belonged to a process
that may have died before, at, or after it. Playing the pulse at
rehydration "because it was clean and he never heard it" was
considered and rejected: it would sound a completion with no finish
present — the pulse decoupled from its event, apparently announcing
whatever he is looking at *now* (possibly the next session arming).
The slip renders settled (Z6) and stands in the same silence as a
settled fresh slip. This row also locks the §4 cure against a subtle
regression: no deferred/persisted "pulse owed" flag may ever be
introduced for a faulted-or-interrupted finish. The pulse is owed to
no one; it is spent in its window or it does not exist.

**Rows 10–12 — the negative space is load-bearing.** Branch-U late,
GRACE_CANCEL, VALVE_BROKE, ENDED_EARLY, MISSED: no E-clock runs, so
no row of this map can leak a sound into them. Stated explicitly
because the §4 cure restructures the sound job — the builder must not
"unify" these paths into the cured sequence. GRACE_CANCEL's silence
is binding by name (item 21): a free cancel inside the opening window
is the machine pretending, correctly and completely, that nothing
began.

---

## 4. THE PULSE-ORDERING CURE — the ordering guarantee the VM must provide

Audit item 14's cure, specced as a guarantee, not a suggestion. This
is VM-side only: no engine change, no new voice, no timing change in
the normal case. Group B4 carries it in the audit; this section is
its buildable form.

### 4.1 The guarantee (normative)

For every witnessed finish, the VM guarantees:

> **G1 (no pulse on credit).** `playCompletionPulse()` is invoked for
> a session **only if**, at the moment of invocation, the detention
> state already satisfies: `finishPulses(kind, row) == true` **∧**
> pawl-lift P has been observed (`MonolithShieldService.instance`
> read null by the detention loop) **∧** the chain has not halted.
> Once P is observed and the chain is unhalted, `isFaulted` can never
> become true for that finish — so a pulse fired under G1 can never
> be contradicted by the slip that follows it.
>
> **G2 (pulse before paper).** The invocation, when it happens,
> happens strictly before `publishReceipt`/`emitReceipt` for that
> finish — the pulse is heard inside the detention's stillness,
> never as the issued slip's sound. (§4.2 shows the existing
> issuance arithmetic makes this automatic.)
>
> **G3 (fault ⇒ never, not "later").** If the fault branch is taken
> (no P by E1520, or halt), the pulse invocation count for that
> session is **zero, permanently** — no catch-up at issuance, on
> retry success, on rehydration, or on any later process's recovery.
>
> **G4 (one decision point).** Eligibility and verdict are read from
> **one** shared detention state, by the sequence that fires. The
> present structure — a detached sound `launch{}` racing the
> detention loop on a private clock reading — does not satisfy G4
> and must not survive the cure in that form.

### 4.2 Mechanism (the smallest restructure that provides G1–G4)

Replace the detached sound job (`MonolithViewModel.kt:1247–1262`)
with **one ordered sequence** driven by the detention loop's own
observations — either by folding the sequence into that loop, or by
having the loop complete a per-finish signal (P-observed / faulted)
that the sequence suspends on. A third `delay()` racing a shared flag
does not meet G4. The sequence:

1. **E0:** fire the knock immediately (row 1). Unchanged.
2. **Suspend until the gate resolves:** P observed, or fault declared
   (E1520 passes with no P, or halt). No clock-only path past this
   step exists.
3. **Fault branch:** sequence ends. Pulse and pair slots stand empty
   (rows 3–5 as applicable). Nothing is scheduled for later.
4. **Gate passed, pulse-eligible kind:** wait until E700 if P arrived
   early (`max(E700, P)`), then fire the pulse, once. Then fire the
   pair **180 ms after the pulse** (the shipped E700→E880 interval,
   re-based on the pulse rather than on E0).
5. **Gate passed, non-eligible kind (PARTIAL / death-booked):** fire
   the pair at **max(E880, P)**. No pulse precedes or follows it.
6. The existing **<900 ms drop-guard (L1252) is deleted**, not
   re-based: under the gate, a pulse at E1100 because P landed at
   E1100 is lawful and required; scheduling overshoot is no longer
   an eligibility fact in either direction.

**Why G2 is free:** issuance is
`min(max(E1480, max(E1200, P) + 280), E1800)` (`Detention.receiptAtMs`,
`ReceiptState.kt:210–213`). For P ≤ E1200, issuance ≥ E1480 > pair-end
(E880 + 90 + knock tail). For P in (E1200, E1520], a pulse-eligible
finish fires its pulse at P and its pair by P + 180 + ε, while
issuance = P + 280 (capped E1800): pair still lands first. For a
non-eligible finish, pair at P < P + 280. In every branch the last
sound precedes the first feed frame — one thing, then the next, the
detention's rhythm kept (cf. Feel-1 §3).

**Normal-case invariance (the feel does not change):** when P lands
early — the shipped, expected case — the cured timeline is
sample-identical to today: knock E0, pulse E700, pair E880/+90. The
cure only moves sounds in the branch where today they are *lies*:
the slow or failed stop. Timings in this map are **floors, not
promises** — E700 means "never before the verdict permits, and never
before 700 ms," in that priority order.

### 4.3 Edge cases the builder must hold

- **P observed at E1519 vs the E1520 deadline:** the detention loop's
  own sampled P is the only P that exists (G4). If the loop records P
  before declaring fault, the finish is clean and the pulse fires at
  P; if fault is declared first, G3 — zero, even if the service dies
  a millisecond later. No second reader may re-litigate this.
- **Halt (`halted`) at any point before the gate resolves:** fault
  branch, even if a P reading races it — a halted chain's stop is not
  a confirmed stop.
- **Pulse-eligible row, gate passes, then the process dies before
  E700/P+ε:** the pulse simply never fires; the restored slip is
  silent (row 9). This is a lost sound, not a wrong one — the correct
  failure direction, and no "owed" state is recorded (G3's mirror).
- **SLEEP stand-down & routine kinds** pass through the same gate:
  they are pulse-eligible today (`finishPulses`), and a stand-down
  whose shield fails to stop is faulted like any other — its pulse is
  withheld identically. (Whether clean stand-down *slips* should
  ambush the waking man is B6's question, a host-gate matter; their
  sound follows their issuance exactly as specced here.)
- **Sound-effects OFF** changes none of the above: the gate governs
  the call; the engine's `soundEnabled` governs only whether the
  call's acoustic half renders (§0).

### 4.4 Flagged extension beyond the audit text (for Zettel)

Item 14 names the **pulse**. This spec also P-gates the **E880 pair**
(steps 3–5), because the pair's filed meaning is the shield's stop
landing and an ungated pair makes the same claim one rank down. If
Zettel rules the pair is merely "the session's machinery settling"
(it can be heard that way), the fallback is: pair stays clock-fired
at E880 unconditional, pulse cure exactly as §4.1–4.3. The pulse
guarantee does not depend on this flag either way; the pair's timing
in the normal case is identical under both readings. Builder does not
improvise between them — the flag is answered before the cure merges.

---

## 5. What is NOT built (rejected, with reasons, so it stays rejected)

- **No feed sound, no settle sound, no dismissal sound** — §3 rows
  6–8. No new engine primitive is proposed anywhere in this map.
- **No fault sound** — §3 row 3. Silence after the register knock is
  the fault's acoustic form.
- **No PARTIAL/death variant of the pulse** (quieter, shorter,
  lower) — §3 rows 4–5. The reserved voice has one form or it has
  no meaning.
- **No catch-up, deferred, or "owed" pulse** on retry, rehydration,
  or next launch — G3, §3 row 9.
- **No tear on dismissal** until/unless B1 is ruled — §3 row 8.
- **No per-line, per-deflection, or counting sounds** on the slip:
  deflections print silently; the slip never ticks as it "prints."
  The feed is one whole-document motion (Feel-1) and one whole
  silence (here).

---

## 6. Acceptance checks (paper checks for the builder's proof)

1. **Fast stop, clean manual:** P at ~E300 → timeline is exactly
   knock E0, pulse E700, pair E880/+90, feed at E1480. Byte-for-byte
   the shipped feel.
2. **Slow stop, clean manual:** P at E1100 → **no** pulse at E700;
   pulse at E1100; pair at E1280; issuance E1480 — pulse and pair
   both precede the first feed frame (G2).
3. **Very slow stop:** P at E1400 → pulse E1400, pair E1580,
   issuance E1680 (P + 280). Still ordered; nothing overlaps the feed.
4. **Failed stop:** no P by E1520 → faulted issuance E1800; total
   lifecycle sound = the E0 knock alone. Pulse count 0, pair count 0
   — acoustic **and** haptic (check both channels; a vibrating fault
   fails this check).
5. **Retry silence:** the E1800-faulted session of check 4 later
   releases via the retry cadence → no sound of any kind at the
   retry's success.
6. **PARTIAL / death-booked, instant P:** knock + pair only; the
   E700 slot is empty even though the gate opened early. Same check,
   shield then failing to stop → row 3: knock only.
7. **GRACE_CANCEL:** cancel inside the grace window → no E-clock
   exists; no knock, no pulse, no slip, no sound from this lifecycle.
8. **Feed/settle/dismiss/restore sweep:** on a clean finish — feed
   (280 ms) silent, settle silent, dismissal (each route, incl. the
   faulted slip's explicit act) silent; force-stop with the slip
   standing, relaunch → restored slip appears with zero sounds.
9. **Branch-U late:** finish discovered ~5 min old → claim + issue
   at discovery, zero sounds.
10. **Gate race:** P landing within one detention-loop tick (25 ms)
    of E1520 resolves by the loop's single reading (§4.3) — whichever
    way it resolves, the sound sequence and the printed `faulted`
    flag agree. A pulse under a faulted slip, or a faulted slip whose
    pair sounded after the verdict, fails.

---

## 7. Dependencies & custody

- **Rides with B4** (audit group B: fault line + pulse-ordering cure)
  for Ren's ruling; the mechanism in §4.2 is merge-shaped but does
  not merge ahead of that ruling.
- **§4.4's pair-gating flag** is Zettel's to answer before merge;
  fallback stated there.
- **Composes with A7/Feel-1** (feed) and **A8** (dismissal gesture):
  those cures must land without adding sound call sites to
  `ReceiptSheet`, its host, or the dismissal routes.
- **No dependency** on C1–C3, B1 (tear), B2 (slip book), B5 (PARTIAL
  evidence) or B6 (stand-down host gate): each, if ruled in later,
  inherits this map — the book re-reads a slip in silence (it is a
  restored slip, row 9), the tear spends its own voice only under
  B1's cure, PARTIAL's evidence line prints without a sound.

— Feel Specialist 4 (sound), Zettel's cell (issue #10).
Paper only; nothing compiled.
