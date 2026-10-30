# Zug — Critics, accessibility — PHASE 2: MaxImpl plan parts vs the three standards

Re-seated, no cold restart. My phase-1 audit (`plan/critics/zug-hold-exits.md`,
23 exit surfaces) and CRITICS-PLAN §2.2 are the measuring instruments; the
standards are his, settled: **ARM ≤10s cold · HOLD absolute, zero mid-session
decisions · TELL THE TRUTH once** — plus his banked-hold law verbatim:
**15s checkpoints bank progress, a slip DRAINS, 60s total, 1 token/week,
target ≥88dp.**

**Procedural note, stated once:** no coordinator synthesis from Voss has
landed. `plan/` holds the ten seat files and `LEDGER-maximpl.md` (a costing,
not a synthesis) and nothing that reconciles them. Where seats contradict
each other below, that is not my invention — it is what an unsynthesised
cohort looks like, and the reconciliation debt is Voss's.

**Mail state:** Prima's phase-1 kill-shot (pre-flight after the slide) was
answered by my phase-1 seating — the reply sits in her inbox
(`zug-re-preflight-after-the-slide.md`). Its substance is re-filed here as
violation V8, because Ines's arming spec never took it up. Hale's safe-break
note was answered in my phase-1 addendum.

---

## Violations table

| # | Seat / owner | Quote (their words) | Standard broken | Reasoning | Fix / kill |
|---|---|---|---|---|---|
| V1 | **Ines** (valve/token), repeated by **Freya** §3.7–3.8 | "The ViewModel re-checks the token in `confirmDisarmSession` regardless (defence in depth, kept)" · "ViewModel already decrements the token *only* on completed non-grace disarm" | CRITICS-PLAN §2.2 token row + Kill #3: check enforced **in the repository, never in a UI prop**; spend recorded in the session's truthful line | The re-check does not exist. Phase 1 measured `confirmDisarmSession` (VM L944–949): at tokens = 0 it *still disarms* — the decrement is conditional, the disarm is not. Ines keeps that shape, calls it defence-in-depth, and makes the real gate the valve view's touch refusal — the exact inversion §2.2 killed. Persistence she specs (see Hunt 1); enforcement and the spend-record she does not: "this element records nothing beyond the token itself," recording deferred to the Ledger's fate. A spent token that no session line names is a fact with no rendering — TRUTH once, violated by omission | Hard precondition inside the repository's disarm: no token, no disarm, full stop. UI refusal stays as decoration. Spend written into the session's one truthful line at completion, Ledger verdict or no — the session row is engine state under every outcome (Freya's own §5 proves it) |
| V2 | **Ines** (valve), **Freya** (host) | — (absence: Element 4's full spec — state, motion, haptics, sound, depth, edge cases — contains no `contentDescription`, no semantics, no checkpoint announcement; grep confirms zero accessibility provisions for the valve in either file) | Phase-1 valve row: "Operable by everyone" — my hardest failure. CRITICS-PLAN §2.2: rebuild includes "checkpoint announcements" | Phase 1: the raw Canvas valve is invisible to TalkBack and Switch Access; the countdown is drawn pixels only. MaxImpl rebuilt every mechanical property of the valve and left this one exactly as found. Freya specs a `contentDescription` for the desk-clock hero (§1.11) — she knows the grammar; it simply never reaches the one surface a trembling or switch-using hand *must* operate to exit. The banked law is the accessibility feature; an unannounced bank is a bank a blind user cannot feel arriving except through haptics they may have off | Valve view: focusable, `contentDescription` stating banked + owed ("Banked 30 of 60 — hold to continue"), accessibility announcements at each 15/30/45 bank and at drain-to-floor, progress as a semantics range. Non-negotiable line item in the build spec, not a polish pass |
| V3 | **Ines**, **Freya** §3.7 | "Process death mid-attempt: bank dies (session is gone or restored without it — either way the attempt's bank is not resurrected)" | §2.2: "bank at 15/30/45 **into persisted state**" | The bank survives overlay dismiss/reopen (VM-owned — a real advance) but not the process. The session itself survives process death in Room (Freya §1.11 relies on exactly that), so the man who banked 45s, whose app was killed by the OEM at minute 40 of his block, reopens to a session that claims it held — and a valve that owes him nothing. His 45 banked seconds evaporate while the session record persists. Persisted session, volatile bank: the two halves of one fact with two different durabilities | `bankedSeconds` rides the session row (or the same small persisted store Ines already creates for the token + Freya for the receipt id). One Int, written at each bank crossing — three writes per attempt at most |
| V4 | **Marko** (loadout) — profiles: **nobody** | "APPLYING reuses it as-is" (of the Shield host `update` block mapping levels → `SmartLoadout`) · the word **profile appears zero times** in 03-marko-loadout.md | Kill #5 + §2.1: loadouts/profiles join the ratchet **at the repository boundary**; my phase-1 fix: "one shared 'would this loosen anything during an active session?' check at the repository boundary, so no future caller can reopen the door" | Marko's LOCKED state is a property of the *view*: "touch swallowed, returns true, no feel, no travel, no callback." Admirable — and it guards the band, not the mutation. `applySmartLoadout` in the repository remains callable by any present or future caller mid-session, exactly as in phase 1, when the caller was a composable just like this one. And the second door from my audit — `applyProfile` (VM L842–849 → Repository L798–815), one tap, every out-of-profile app OPEN — is not mentioned by any of the ten parts. The widest hole got better joinery on one of its two doors, at the wrong layer | The gate moves to `MonolithRepository.applySmartLoadout` / `applyProfile`: while a session is active, any loosening is refused there, tightening applies. Marko's view lock stays as the rendering of that law. Voss: assign the profile door to a named seat before build |
| V5 | **Voss** (cohort, unsynthesised) | Freya §3.1: SHIELD "**Refused**… tightening is also a decision, and HOLD admits none" — vs Yuki: "Strengthening throws during a session work normally" — vs Marko: band LOCKED in both hosts — vs Rurik FROZEN: DONE/DEL/SPLIT "absent" mid-session — vs Freya §2.1: "DONE stays" — vs Alba: "this spec's position is that the composer stays live" mid-session | HOLD absolute — one law, one meaning | Six seats, at least four incompatible answers to the same question (what may be touched while sealed), all filed as settled within one cohort plan. Freya's Cloister makes Yuki's mid-session sled behaviour and Alba's live composer unreachable code; Rurik freezes what Freya keeps as a lawful report. Both cannot ship. This is precisely the failure a coordinator synthesis exists to catch, and none has landed | Voss rules one Cloister-or-ratchet model before build; the losing specs are struck in their files, not left as parallel truths |
| V6 | **Sten** §5.5 (routines) | "On the 3rd consecutive MISS… `enabled = false`, `autoPausedAtMillis = now`" · footer: "THIS ROUTINE PAUSED ITSELF AFTER 3 MISSED WINDOWS." | CRITICS-PLAN §5, charge **C3**: "R3.24's '3 misses → auto-pause' is the machine editing his routine — a decision taken at his tiredest. Lawful form: a printed fact (`MISSED ×3`) he acts on" | Sten rebuilt the condemned mechanism whole — streak counter, receiver-side disable, self-pausing copy — and filed it *after* C3 was on the record. His defence ("the rested user's own schedule defending itself") is the Consent-Beat move in new clothes: the machine deciding at the tiredest moment, justified by the rested self's general intent. C3 already priced this: the fact may print; the switch may not throw itself. He even specs the lawful half — line 4 prints the streak — then adds the unlawful act on top | Kill the auto-disable. Keep §5.2–5.4 entire (the ghost-kill is exemplary — Passes). Line 4 prints `MISSED ×3` / `3 MISSES IN A ROW` as a fact; `enabled` changes only by his hand on the sled |
| V7 | **Ines** Element 2 (NEXT ribbon) | Grammar: "`NEXT {HH:MM} · {LOADOUT} · FIRES ITSELF`" — and state 6: "`NO ROUTINE SET — NOTHING FIRES ITSELF`" | CRITICS-PLAN §5, charge **C1**: "R2.17's 'FIRES ITSELF' ribbon advertises the machine-agency he killed. NEXT may print facts (time · loadout)" | The condemned copy survives verbatim into the implementers' spec — the exact "laws quietly redefined by the builder's side" failure this phase exists to catch. Ines's own truth rule 2 concedes the fire "can land minutes late" under Doze while the line claims agency in the present tense. Sten §5.6, same cohort, rules the opposite: lines "never print `FIRES AT 05:00`… never use the word ARMED about a future firing." The cohort disagrees with itself in print | `FIRES ITSELF` dies. Ribbon: `NEXT {HH:MM} · {LOADOUT}` — time and loadout are facts; self-firing is a claim. Sten's §5.6 wording ceiling governs all routine lines app-wide |
| V8 | **Ines** (arming) | — (absence: zero mentions of pre-flight, primer, or permission in the arming spec; her budget line prices only "Deck open → … → one slide") | ARM ≤10s **cold** — Prima's audit, CRITICS-PLAN §1 A-1: permission health is a standing Deck fact; the slide is never spent on a modal | Her ≤10s claim is true of the gesture and false of the morning. Phase 1 priced the permission-dead path at 9–12 gestures, ~12–35s+ — 2–3× the entire budget — because `requestArmWithPreFlightCheck` checks accessibility *after* the slide commits. The seat that owns arming specced gate geometry to three decimal places and never moved the check. ARM cold means cold: the worst morning, not the rehearsed one (see ARM pricing below) | Pre-flight becomes a standing Deck fact (dead guard shown on the idle Deck before any slide is offered); the Settings trip leaves the arm path; a pending arm completes on return with no second slide. Prima's letter, my phase-1 reply, and A-1 already spec this — adopt, don't re-derive |
| V9 | **Ines** + **Freya** (valve host) | Ines: "Return chip: `KEEP FOCUSING`" · Freya §3.4: chip states specced (live / inert at 35% / only action) — **neither file gives the chip a height** | Phase-1 §f + Kill #14: component floor ≥48dp; the safe button was **42dp** (`BlockitActionChip` hard-codes `height(42.dp)`) | The one target my phase-1 table named first — the *stay* button, smallest target on the exit screen — survives the entire MaxImpl wave unmeasured. Both owners spec its wording, its alpha, its inertness, and never its size. A builder following these two files faithfully ships KEEP FOCUSING at 42dp again | `BlockitActionChip` default height → ≥48dp in the shared component (one change heals every chip, phase-1 fix verbatim); Freya's host spec states the valve card's chip at ≥48dp explicitly |
| V10 | **Rurik** (rows), **Sten** (editor), **Otis** (chrome) | Rurik: step rows "minimum height **34dp**, full-width tap target" · WordActions "≈**37dp** touch height" · queue rows "exactly **44dp**" · Sten: time keys **40dp** tall, calendar cells **44dp** · Otis: HELD/GUARD "invisible hit boxes keep the **32dp** height and ≥48dp width" | Generic accessible floor ≥48dp (phase-1 §f; Sten himself states it: "≥ 48dp target law") | The cohort knows the law — Sten invokes it for his rows, then specs 40dp keys and 44dp calendar cells two sections later. Rurik's 34dp step target is the worst single number in the wave: a sub-step toggle is a *mid-session* tap (his own FROZEN state notwithstanding, Freya keeps toggles live), made by the tired hand this whole app is designed around. Otis buys width and forgets height. Note the honest exceptions: Yuki's sled track is 36dp *visual* but its touch rect is the full 52dp row height — that passes, and I say so; Marko's 32dp cap rides a 104dp plate that is the hit area — passes; Alba's caps are exactly 48dp — passes | One sweep, builder-level: no tappable row, key, cell, or word-action under 48dp in height. Rurik steps → 48dp min; WordAction padding → 48dp touch; Sten keys → 48dp, calendar cells → 48dp; Otis hit boxes → 48dp high. Where a visual must stay small, the touch box grows invisibly — Yuki already demonstrates the pattern |
| V11 | **Yuki** (sleds / Shield header) | The deflection readout is "sourced from the existing `MonolithViewModel.totalInterceptions` flow (**max of intercept-log size and Σ `interceptCount`**)" | CRITICS-PLAN T-3: "Deflections = `max(logs.size, Σ per-app interceptCount)`… two rival ledgers, print the larger" — replacement: one ledger, one count | Yuki re-homes the condemned number to Shield's header and calls the placement truthful because "the label makes no time claim." The label was never the lie; the `max()` is. Two rival ledgers printing the larger survives the wave wearing a new address | Source the header from the intercept log alone (the one ledger). If Σ `interceptCount` and the log disagree, that disagreement is a data bug to fix at the repository, not a `max()` to render |
| V12 | **Otis** (war line) | "`186D TO NEET · TODAY 2H30 / 12H`" — TODAY sourced from "this app's own session records" | CRITICS-PLAN §5, charge **C5**: "Chrome's `TODAY 2H30 / 12H` is a lie with a denominator — YPT owns his day; Monolith may print app-scoped `SEALED 2H30`, **labelled**, or nothing. `186D TO NEET` prints an unannounced exam date as fact or dies" | Half-fixed, and the unfixed half is the label. Otis honestly re-sources TODAY (own sessions, live session included, floored `totalFocusedMinutes` explicitly refused — real work, see Passes) and stores the exam date with a confirmed flag. But the line still prints `TODAY`, which claims his *day*; the source measures only what Monolith sealed — on a day he studies outside the app, the plate understates his day while wearing its name. And the unannounced date still prints as a day-count; secondary ink is a whisper of doubt on a number rendered at hero adjacency — C5's terms were *labelled or dies*, not *dimmed* | Print `SEALED 2H30` (the app-scoped word C5 named). Days segment prints only when `neetExamConfirmed`, or carries its status in words on the line's own home per Otis's correction row — which he already built; promote its wording onto the plate until confirmed |
| V13 | **Freya** §4 preamble (receipt) | "a grace-cancelled session never happened — the repository already writes it as zero banked seconds with no log line" | Kill #13 + my grace row: "JUSTIFY the window, **KILL the erasure** — it leaves one truthful line" | Freya states the erasure as a *virtue* ("never happened") and builds the receipt's purity on it. A man who armed, realised at second 7, and cancelled has a fact in his day; the record that zeroes it is the record lying by deletion — the same class as Sten's ghosts, with the sign flipped. §2.2 settled this: the window is lawful, the vanishing is not | Grace cancel writes its line ("Cancelled in the grace window, Ns in, no token spent") with real elapsed. The receipt stays completion-only — purity of the *receipt* never required falsity of the *record* |
| V14 | **Freya** §2.1 (Deck task line) | "DONE reports a fact about the past… `completeTaskAndAdvance` advances automatically" — DONE + immediate advance kept; Linea's removal overruled | CRITICS-PLAN §2.2: "DONE/sub-steps: **execution, justified** (toggle only, never edit text). The *advance* is banked to block-end so the queue can't be browsed forward at his weakest" | Freya's report/decision test is a good argument for the toggle and no argument at all for the advance — she defends the pair as one act. §2.2 split them: the report is his, the *reordering of his future* mid-session is a decision surface. Her overrule also never mentions the banked-advance settlement it replaces. (Rurik's opposite error — freezing DONE entirely — at least errs toward HOLD; the cohort holds both, see V5) | DONE toggles and reports in place; the queue advance lands at block end. If Voss re-opens this, it is re-opened as a named charge against §2.2, not absorbed into a rationale for the toggle |
| V15 | **Unowned — Voss** | — (absence across all ten parts: no seat kills the three seeded fake completed sessions, the `totalFocusedMinutes` floor of 35 / initial 75, or the deflections initial of 6) | Kill #1 (seed data dies), Kill #2 (every floor dies), T-1/T-2/T-3 | Sten reseeds the *routines* honestly (§4 — real pass). The *session* seeds — 150 fabricated minutes in the history table, indistinguishable forever — are mentioned by no MaxImpl part. Otis routes the war line around the floored flow without killing the floor; the drawer still spends it (he flags this himself, then assigns it to "the drawer's owner" — no such seat exists in this cohort). A falsified record that every surface is taught to walk around is still a falsified record | Voss assigns the record store (seeds out, floors out, one deflection ledger) to a named seat before build; "route around it" is not a disposition |
| V16 | **Ines** Element 3 (demotion) | Inside an open routine window the slide is "replaced… **Tap** (not slide) arms a session for the remaining window" | ARM standard's centre gesture (Ren's verdict: "The slide and the seal are the centre gesture") + the one-commit-path discipline Rurik states for Tasks: no cheaper second arm path | Priced in gestures, the demoted tap is the cheapest arm in the entire plan — one tap, no travel, no gate — offered at the exact moment the rested user's decision is *least* in doubt but the tired user's state is least examined (he is already inside the window, possibly because the session broke early). The reasoning ("the commitment was already made") proves too much: by it, mise en place could arm itself. A second, cheaper arming gesture is how the centre gesture stops being the centre | The demoted slot keeps the slide, pre-laid to the remaining window (mise already does the laying-out). If a tap-arm is truly wanted, it is a change to the ARM standard itself and goes to Sarthak as such — not an Element-3 footnote |
| V17 | **Freya** §1.6/§3.1 (desk clock) | "Tap-anywhere-to-exit is the overlay's only interaction" — kept; exit hint reduced to a 4-second ghost | CRITICS-PLAN §2.2: "Desk-clock tap: whole screen dismisses the AOD into the full live Deck UI — **deliberate gesture to exit** (long-press or the valve's little sibling), not any tap" | Partially mitigated, and I record the mitigation honestly: under Freya's own Cloister the Deck he lands on is locked to the running state, so the phase-1 harm (a stray tap opening the full live app mid-session) is mostly disarmed. What remains: any contact — a sleeve, a palm, the phone face-down-and-up — ends the AOD study display the user deliberately opened (his Oct 28 anchor: the desk clock is a *study display*), with no gesture distinguishing intent from accident | Long-press (or press-and-hold 600ms) to exit; a plain tap does nothing. The ghost hint then teaches the real gesture in its 4 seconds |

---

## The five hunts, answered one by one

### Hunt 1 — The token: does any part persist it, or keep it in memory while calling it weekly?

**Persisted — on paper, for the first time in either wave.** Ines Element 4,
"Persistence (required delta)": *"store `tokensRemaining` with
`weekStartEpochDay` (Monday 00:00 local…)"* in "DataStore or a Room row";
on week mismatch the token resets to 1. Freya §3.7 endorses the same delta
in the same terms. The week anchor exists, the Monday boundary is named,
banks explicitly do not carry across sessions, and the token is spent
*only* on a completed break — banking, slipping, draining, and dismissing
all consume nothing. That is his law, specced.

Three holes keep the charge from closing (V1, V3's sibling):
(a) **Enforcement stays inverted** — the gate is the view refusing touch;
`confirmDisarmSession`'s non-enforcement is misdescribed as a re-check
"kept." The repository precondition §2.2 demanded appears nowhere.
(b) **The spend is recorded nowhere** — deferred to the Ledger's fate,
though the session row exists under every fate.
(c) Ines's hedge — *"if Ren's build uses a different stored convention
already, follow the store, not this sentence"* — points at a store that
does not exist (phase 1: no persistence, no week, no timestamp anywhere).
There is no convention to follow; the sentence invites a builder to go
looking for one and improvise. Strike the hedge; the delta is the spec.

**Verdict: the token is built as a counter and not yet as a law.**

### Hunt 2 — The valve: banked law, or the continuous valve renamed?

**The banked law, genuinely — the strongest element in the cohort.**
Ines Element 4, checked line by line against his settled rule and against
the Barriere motor numbers Critics adopted:

- Banks at 15/30/45 into VM-owned state; banked seconds *never* decrease
  inside an attempt; the bank survives overlay dismiss/reopen.
- Slip grammar: **800ms no-drain grace** → drain at **2 banked-seconds per
  real second**, unbanked only, **stopping dead at the bank floor** →
  250ms re-seat on re-grab. These are the HUMANS/Barriere numbers (800ms,
  2.0× drain) adopted, not re-derived — exactly what §2.2 ordered.
- Target **88dp** for the bar itself (144dp total valve view), full width,
  no sub-target — "a hold target you must aim at is a target designed for
  calm hands," which is Rosa's wall point and mine, landed.
- Checkpoints are *feelable*: segment pitch climbs 420→660Hz and resets at
  each bank; banks land at falling pitch (560/470/390Hz) with multiplying
  latch strikes (1/2/3). Phase 1's flat sawtooth is dead.
- Pointer discipline phase 1 found broken both ways is fixed: first
  pointer owns the hold, second finger ignored, slide-off = `ACTION_CANCEL`
  = slip (grace applies, no distinction of blame).
- Copy rewritten truthfully: "continuously" and "physical" die; the rule
  line states banking and draining in plain words; the owed numeral counts
  *up* during drain, in the open. Freya's host converges on the same
  machine (one machine, three owners: VM owns the bank, view owns the
  grip, overlay owns the visit) and deletes the completion double-sound.

What it still lacks is V2 (no accessibility semantics or announcements —
the valve remains unusable by TalkBack/Switch Access and unannounced for
everyone) and V3 (the bank dies with the process while the session
survives it), plus V9 (its host's safe chip unmeasured).

**Verdict: they did not rename the continuous valve. They built his —
and left my lens's half of it unbuilt.**

### Hunt 3 — The loadout ratchet: repository boundary, or the widest hole with better joinery?

**Better joinery, at the view.** Marko's LOCKED state is genuinely good
*as far as it reaches*: the band freezes at the slide's gate crossing (the
payload is captured there — a detail nobody else in either wave thought
of), a gesture begun before the crossing is cancelled into RETURNING with
no callback, routine firings queue and travel only at session end, and
APPLYING commits at the seat so the loadout can never apply before the
cap arrives. Ines's mise en place is equally disciplined: preview only,
*"Nothing is applied"* — `applySmartLoadout` is never called early.

But the ratchet §2.1/Kill #5 demanded lives at the **repository
boundary**, and no part puts it there. Marko reuses the host `update`
block "as-is"; the repository's `applySmartLoadout` remains an ungated
wholesale rewrite one caller away from phase 1's one-tap dismantling.
**Profiles are not mentioned by any seat** — the second door stands
exactly as I left it in phase 1, unexamined. Yuki's sleds keep the
per-app ratchet (correctly — it was phase 1's one lawful mechanism), and
Freya's Cloister, if it ships, makes the whole question unreachable —
but V5 stands: the cohort never decided which of those is the plan.

**Verdict: the hole is narrower (one door, view-guarded, payload-frozen)
and still open at the layer that matters. My widest-hole charge stays
open, downgraded from *unguarded* to *guarded at the wrong layer, with
the profile door untouched*.**

### Hunt 4 — Routines: CRUD whole and ghosts killed, or the fraud automated?

**Both halves of Sten's file are real — the ghost-kill is the best
truth-work in the wave, and one mechanism in it is condemned machinery
rebuilt.** Take them separately, because Sten earned both verdicts:

*CRUD — whole.* Create from scratch as an unsaved draft (not in the DB,
drawn dashed, counts nothing — draft ≠ saved in data *and* in drawing),
duplicate with honest non-inheritance (pause and attendance do not copy),
rename with the name-match fence stated on its face, delete with a
5-second undo instead of a confirmation dialog, pause-until-date with a
real calendar, direct-tappable loadout cells replacing the cycle-only
fallback. The DAO/VM surface is named function by function, and the
`fallbackToDestructiveMigration(true)` trap — which would have wiped the
very rows his reseed migrates — is flagged as a precondition, with an
upgrade test. The reseed itself is exact-match-or-untouched: the machine
rewrites only what the machine wrote. This is Kill #8 executed in full.

*Ghosts — killed at the source, not the rendering.* §5.2–5.4: attendance
is a per-routine record created only for scheduled windows; presence is
*sampled evidence* (E1 session interaction, E2 foreground return, E3
intercept handled, E4 emergency interaction) with a 5-minute decay;
machine acts explicitly never count; MISSED sessions are **zeroed**
(`elapsedSeconds = 0`, no artifact, no intercept-log line), PARTIAL
sessions rewritten to attended seconds, and the failure direction is
specced — *"if the service dies, sampling stops and attendance
undercounts. When in doubt, book less."* The late-fire remainder fix
(a 07:50 firing no longer seals until 10:50) and the HELD outcome
(a routine held by his manual session books nothing) close phase-1
charges T-7 and T-8 in substance. This answers CRITICS-PLAN C4 the way
C4 demanded: presence evidence, defined.

*The fraud that remains is §5.5* (V6): three misses and the machine
disables his routine — C3's condemned auto-pause, automated more
efficiently, with an enum trick (`MISSED_AUTOPAUSED`) to make the grudge
forgettable. And §7 deletes the manual theme override wholesale on the
same logic (the schedule decides; no pill, no setting, no lever but
pausing SLEEP itself) — defensible under rested-decides, but it lands
while Otis's branches still carry override rows "only if Sten's rule
keeps one": another unreconciled seam for Voss.

**Verdict: CRUD whole ✓ · ghosts killed ✓ · the fraud is not automated
— except the one fraud C3 named, which is.**

### Hunt 5 — "Implementation details" contradicting phase-1 measurements

Five, in descending order of harm:

1. **Targets below 48dp ship throughout** (V10): Rurik steps 34dp,
   WordActions ≈37dp, queue rows 44dp; Sten keys 40dp, calendar 44dp;
   Otis hit boxes 32dp high. Phase 1's floor was not a preference; three
   seats re-violated it while a fourth (Sten) quoted it.
2. **The 42dp safe chip is carried silently** (V9) — phase 1's named
   first target, unmeasured by both its owners.
3. **Yuki's deflection header re-sources T-3's `max()`** (V11) — a
   measured lie from the data layer, re-homed as a placement decision.
4. **Otis's war line keeps C5's condemned form** (V12) — sources fixed,
   labels not: `TODAY` for an app-scoped sum, an unannounced date as a
   day-count in demoted ink.
5. **Freya's receipt preamble re-blesses the grace erasure** (V13) and
   her §0.3 fix, while genuinely killing the per-block FINISHES lie,
   leaves the tick architecture it describes untouched elsewhere — noted,
   not charged: her derivation (penalty-aware `effectiveTarget`, session
   remaining including breaks) is the correct repair and I record it as
   a pass below.

Tick rates, checked where specced: Ines's valve runs the shipped 32ms
runnable with a 250ms drain tick — consistent with phase-1's measured
machinery. Dario's detent throttle (28ms, dropped-never-queued) and
density law stay inside the vibrator's honest limits and say so. No seat
invented a tick the engine cannot make. On seeds: routine seeds are
reseeded to his real day (Sten §4 ✓); **session seeds survive untouched
by all ten parts** (V15) — the one "demo data" charge nobody picked up.

---

## ARM, priced in gestures and seconds cold (Ines's spec, my verification)

| Path | Gestures | Seconds | Verdict |
|---|---|---|---|
| Cold open → slide, duration unchanged, permission alive | 2 (launcher tap; one slide, ~166dp finger travel to gate at raw 0.67) + ≤380ms machine (assist travel ≤260ms + 120ms dwell) | ~2–3.5s | **PASS** — and the slide itself is repaired: DOWN counts only on the thumb (phase-1/Moss tap-to-commit hole dies), release-past-gate commits, backward fling vetoes, no spring-back on commit |
| Duration change via wheel | +1 drag (Dario: relative, grab-offset — the atan2 teleport tax is gone) | ~1.5–2.2s total | **PASS** |
| Mise en place laid (T−30 to a routine) | 1 slide only — duration, loadout, task pre-laid | ~1–2s | **PASS** — the fastest honest arm in either wave; preview applies nothing (Hunt 3) |
| Inside an open routine window (demotion) | **1 tap** | <1s | **Passes the clock, fails the gesture law** — V16: a tap is not the centre gesture, and this is the cheapest arm path in the plan |
| FLOW left on → re-dial | 1 tap on FLOW key restores {preset, dial, blocks} (Marko §4 `toggleFlow`) | ~0.3s + slide | **PASS** — phase 1's one-way trap and its ~3.5–4.5s worst case die here |
| **Accessibility service dead at arm** | **not specced by Ines** — phase-1 price stands: slide spent → modal → 7–10 Settings gestures → return → second slide = 9–12 gestures | **~12–35s+** | **FAIL by omission** — V8. The arming spec's ≤10s claim prices every morning except the one the budget exists for |

Net: on the mornings Ines priced, ARM passes with room to spare — her
gate-at-0.65 with release-commit is *cheaper* than phase 1's 0.82 drag
and safer than it. The budget still dies in the one place CRITICS-PLAN
§1 said it dies, because the seat that owned the fix never wrote it.

---

## Passes — what they got right that other cohorts only gestured at

Fairness, plainly. These are not consolations; several are the best work
in the plan wave on any cohort's paper:

1. **Ines's valve mechanics (Element 4)** — banking, drain, grace, 88dp,
   escalating checkpoint feel, pointer discipline, truthful copy: the
   full settled law as a buildable machine, adopting Barriere's motor
   numbers instead of re-deriving them. No other cohort got past
   describing this valve; Ines specced it to the runnable.
2. **Ines's slide (Element 1)** — the tap-to-commit hole, the badge that
   lied (`25M` committing 100), the spring-back on success: all three
   phase-1 findings killed with mechanisms, and the badge rule made
   mechanical ("the badge repeats the TOTAL or it dies").
3. **Sten's attendance machinery (§5.2–5.4)** — presence evidence defined,
   undercount bias specced, ghosts zeroed at the disarm call rather than
   filtered at the rendering. Critics demanded the definition (C4); Sten
   is the only seat in any cohort who wrote one.
4. **Sten's CRUD + reseed discipline** — draft-vs-saved truth in data and
   drawing, exact-match migration, the destructive-fallback precondition
   named before it could eat the upgrade.
5. **Marko's APPLYING-at-seat + gate-crossing payload capture** — the
   loadout cannot apply before the cap arrives, and cannot change after
   the slide's gate crosses. Two lies (apply-before-arrival,
   change-after-commit) killed in one state machine.
6. **Freya's Cloister (§3)** — HOLD enforced at the single `navigateTo`
   choke point as *derived state* (kill the app, relaunch, the Cloister
   is already standing; no boolean set at arm time), with rendering and
   guard required together so neither can lie about the other. As an
   architecture it is better than the ratchet it would replace — which
   is exactly why V5 must be ruled, not left standing beside it.
7. **Freya §0.3 + §1.4** — the per-block entity truth (elapsed resets per
   block; `remainingSeconds` ignores penalty) found, named, and repaired
   with one shared derivation; the desk clock's floored pill bar
   (`coerceIn(0.04f, 1f)`) deleted — "at 0% the line is honestly empty."
8. **Yuki's acceptance-before-sound** — the ratchet's Boolean made
   synchronous so a rejected throw is silent, the ViewModel's double
   detent deleted, the invisible name-tap tier-cycle killed with its
   dead ViewModel functions named for deletion. Plus the wave's most
   complete sled accessibility block (focusable, readout-tier
   description, three spoken actions).
9. **Dario's wheel dispatch** — drawn radius = touch radius on the hub,
   rest annulus dead zone, accessibility step actions through the same
   snap+beat path as touch. The wheel is the one control in the wave
   whose touch geometry was audited as geometry.
10. **Nadia's numeral contract** — the total is Σ printed rows, checkable
    by eye, coercions and floors dying with the bar; and her dissent
    records the one fact the kill destroys (cumulative starts) instead
    of pretending the loss is zero. That is TRUTH applied to one's own
    spec.
11. **Otis's source discipline** — every war-line number given a stored,
    correctable source, and the floored `totalFocusedMinutes` refused by
    name. His failure (V12) is labelling, not sourcing — the harder half
    was done.
12. **Alba's invariant ADD** — the submit target never changes address
    across the composer's two shapes, and the disarmed key *answers*
    (detent + focus) instead of lying silent. A small, complete piece of
    truth-in-interaction.

---

## Open-charge status (my phase-1 charges, after this wave)

| Charge (phase 1) | Status after MaxImpl |
|---|---|
| **The banked valve does not exist** | **Specced shut by Ines/Freya** — mechanics complete. Remains open on two line items only: accessibility semantics/announcements (V2) and bank persistence across process death (V3). In code, still unbuilt — this is a plan audit |
| **The weekly token does not exist** | **Half-closed.** Persistence + Monday week-anchor specced (Hunt 1). Still open: repository-level enforcement (V1) and the recorded spend (V1) |
| **Mid-session loadout/profile application is the widest hole** | **STILL OPEN — still the widest.** Loadout guarded at the view only, repository ungated; profiles addressed by no seat (V4). Cohort additionally undecided between ratchet and Cloister (V5) |
| **42dp safe-buttons** | **STILL OPEN, untouched.** KEEP FOCUSING unmeasured by both owners (V9); the wave added fresh sub-48dp targets in Rurik, Sten, and Otis (V10) |

**Worst violation, one sentence (for Hale):** the widest hole from
phase 1 is still the widest — Marko locks the loadout *band* while the
repository's `applySmartLoadout` stays ungated and `applyProfile` is
mentioned by no seat at all, so one tap can still dismantle a sealed
session's shield while its record claims the seal held.

— Zug, Critics (accessibility), Phase 2
