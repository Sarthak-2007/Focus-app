# THE FUSED DECK — MAXIMUM IMPLEMENTATION POTENTIAL
## MIP-03 · issue #6 (main-app pass — Deck) · Fri Oct 30, 2026
## Ideas maximised: "One Question Per State" (Plan §7) + "Fusion" (Vega A)
## Floor documents: `studio/plan/AUDIT-deck.md` (the audit, ROST) ·
## `studio/plan/DESIGN-deck-rahmen.md` (RAHMEN's composition — adopted, not re-argued)
## Status: SPEC ONLY. Nobody compiles.

RAHMEN closed the composition: what stands where, in which state, at
what volume. This pass closes the layer a composition cannot: **the
fullest version of the fused Deck that can actually be built** — the
mechanism inside the wheel, the state model that makes "one question
per state" a type-system fact instead of a review opinion, the fate
of every one of the 21 dead host parameters, the chrome arithmetic
done honestly, and the price, in lanes and lines, with the demands
this design places on the rest of the app.

Nothing here re-opens an audit verdict or a RAHMEN ruling. Where
this pass goes past both, the step is marked **MAX** and priced
separately, so the merger can strike the MAX layer and keep the
floor intact.

**Boundary, kept:** the top strip is issue #4's cell. It enters this
spec only as an input — a height `S` and a warrant print-state —
never as a surface designed here (§6.3).

---

## 0 · THE MAXIMISED IDEA, IN ONE BREATH

The Deck stops being a screen that *contains* a timer and becomes
**one instrument with nine faces**. A single sealed state type —
one variant per state, each variant carrying *only* the fields its
one question needs — drives a single wheel view that is born at
idle, is set by hand, locks at the seal **without being replaced**,
and then *is* the clock for the session's whole life: the arc you
drew when you set the span is the arc that fills as the span
elapses — one arc, two tenses, same geometry, same address. The
host contract collapses from 61 parameters to 3. The pinned stack
collapses from ~216.5dp to ~119.5dp of Deck-owned chrome, and the
hero card — eyebrow, triple-printed glyph, progress track, `%` —
ceases to exist as a separate object, because its job moved into
the hub. Every fact the old face printed six times is printed by
exactly one component, in exactly the states whose question it
answers, and is *unrepresentable* in the others.

---

## 1 · THE STATE MODEL — ONE QUESTION PER STATE, AS A TYPE

### 1.1 Why flags cannot hold this design

Today's Deck derives its face from ~14 independent inputs
(`activeSession`, `armPhase`, `finishState`, `receiptState`,
`deckWarrant`, `sealFreeze`, `isDarkTheme`, dial, blocks, counts…)
combined by local booleans (`isSessionRunning`, `showRunningForm`,
`heldFinish`, `provingThisSession`, `shotActive`…). That is
2¹⁴ nominal faces, of which nine are real. Every clutter and truth
charge in the audit is a symptom: with flags, any fact *can* render
in any state, so eventually one does — the strip shouting in
health, BLOCK printed six times, the hero and the hub as rival
clocks. **MAX move: make the nine real faces the only
representable ones.**

### 1.2 `DeckState` — sealed, closed field sets

```kotlin
sealed interface DeckState {
    val question: String   // the state's ONE question, for tests/debug

    data class Idle(
        val set: SessionSet,            // dial, blocks, derived span, recipe
        val ghostTitle: String?,        // active task title, read-only
        val endsLabel: EndsLabel,       // Planned(HH:mm) | NoEnd  — F21's cure, typed
        val tenant: ImprintTenant,      // promise (LIVE) — fault never reaches Idle
    ) : DeckState                       // Q: "How long, and into what?"

    data class IdleFault(
        val set: SessionSet,
        val ghostTitle: String?,
        val sentence: RefusalSentence,  // cause-worded, owns its FIX routing (F9)
        val tenant: ImprintTenant,
    ) : DeckState                       // Q: "Why won't it hold?" — see §1.3

    data class Proving(
        val set: SessionSet,            // frozen exactly as dialled
        val proof: ProofSnapshot,       // the drawer's three cells, this attempt only
        val latchedTenant: ImprintTenant,
    ) : DeckState                       // Q: "Is it holding?"

    data class Jammed(
        val set: SessionSet,
        val proof: ProofSnapshot,
        val jamSentence: RefusalSentence,
        val latchedTenant: ImprintTenant,
    ) : DeckState                       // Q: "Is it holding?" — answer: not proven

    data class Grace(
        val session: RunningSession,    // id, stored end, frozen counts
        val graceSecondsLeft: Int,      // 10..1 — the hub prints THIS (§2.4)
    ) : DeckState                       // Q: "Did you mean to start?"

    data class Running(
        val session: RunningSession,
        val readout: Readout,           // Remaining(MM:SS) — hub, §2
        val ghostTitle: String?,
        val heldNotifications: Int,     // HELD's noun-count, utilities only here
    ) : DeckState                       // Q: "How long left, and what's in the way?"

    data class Break(
        val session: RunningSession,
        val breakSecondsLeft: Int,      // the hub IS the break (F5)
        val backAt: String,             // "BACK AT HH:MM", stored-end derived
        val ghostTitle: String?,
        val heldNotifications: Int,
    ) : DeckState                       // Q: "How long is my break?"

    data class FinishDetained(
        val frozen: FrozenFrame,        // last frame, frozen readout + frozen arc
    ) : DeckState                       // Q: "Did it end true?" — acquitted, kept

    data class Receipt(
        val receipt: ReceiptState,      // the slip, as now (acquitted)
        val sledReturn: SledReturnState // witness/try-start, as now
    ) : DeckState                       // Q: "What just happened?" — the drawer's fact
}
```

Supporting types, all already latent in the code, now named:

- `SessionSet(dialMinutes, blocks, spanMinutes, mode)` — `spanMinutes`
  is the ONE formula `n × block + (n−1) × 10` (BEYOND W3, already
  landed in-code as `totalSpanMinutes`); `mode = if (dial < 10) FLOW
  else TIMED` is a **derivation, never a stored field** (ESR-08's
  corpse preset cannot regrow — §3, row 1).
- `RunningSession(id, storedEndMono/wall, isStopwatch, frozenShut,
  frozenPeek, frozenWarrant)` — today's `sealFreeze` + the Deck's
  saveable fallback capture, promoted to one VM-owned value. The
  fallback capture in the composable **dies**; VM scope or it isn't
  a freeze (Emil E-1/E-2/E-3, already the code's own commentary).
- `ProofSnapshot(block, apps, time, seam)` — exists only inside
  `Proving`/`Jammed`. **This is the F16 drawer made structural:**
  the strip's cells cannot render in `Running` because `Running`
  has no field to render them from.
- `EndsLabel` — idle ends are `Planned(...)`, running ends are read
  off `storedEnd` inside `RunningSession`. The audit's sliding-idle-
  ENDS lie (§5.5) becomes a type distinction, not a label discipline.

### 1.3 **MAX — `IdleFault` is its own state**

The audit + RAHMEN treat idle-fault as Idle with a promoted
sentence. Maximised: it is a ninth face with its own question —
*"Why won't it hold?"* — because its verb set differs (the
sentence's FIX tap is live business), its pinned stack differs
(+22dp, §5), and its imprint tenancy differs. Cost: one extra
variant, zero extra UI — the same elements, re-ranked. Gain: the
Lamp Test's fault frame and T6 (fault grows) test a *state*, not a
flag combination; and `Idle` becomes provably silent — in `Idle`,
BLOCK is printed zero times **by construction** (no warrant field
exists in the variant beyond the tenant's own verdict).

### 1.4 The derivation — one `combine`, one place

`MonolithViewModel` gains:

```kotlin
val deckState: StateFlow<DeckState> = combine(
    activeSessionFlow, armProving.phase, warrantPrintState,
    finishState, receiptState,
    dialFlow, blocksFlow, appsSplitFlow, activeTaskFlow,
    capturedCountFlow, routineNameFlow
) { … -> DeckState }   // exhaustive when; ~120 lines
```

Every derivation the composable body performs today —
`provingThisSession`, the frozen-capture block (~90 lines in the
screen), `blockCell`/`appsCell`/`timeCell`, the seal-bar state,
`imprintTenant`, `progressFraction`, the eyebrow's three-way
`when` — moves behind this `combine`, each becoming a field
computed once, in VM scope, testable without composition. The
screen keeps exactly two local states it legitimately owns:
`SealShotState` (the shot is a rendering event, §4.3) and
`PressLatch` (a press is a gesture, §4.4).

**Precedence table** (the `when`, in order — first match wins;
this table IS the spec for the merger):

| # | Condition (existing VM facts) | State |
|---|---|---|
| 1 | receipt standing | `Receipt` |
| 2 | finish detaining | `FinishDetained` |
| 3 | armPhase Jammed (this session) | `Jammed` |
| 4 | armPhase Proving (this session) | `Proving` |
| 5 | session running ∧ in Safe Break | `Break` |
| 6 | session running ∧ elapsed ≤ 10s | `Grace` |
| 7 | session running | `Running` |
| 8 | no session ∧ warrant print-state ≠ LIVE | `IdleFault` |
| 9 | no session | `Idle` |

Routine-born / restored sessions enter at 5–7 directly (born
settled, no shot — existing law, preserved). A session whose
`armPhase` is Idle but which exists (routine/restore) is Running —
row order handles it, since rows 3–4 match on phase first.

### 1.5 The host contract — 61 → 3

```kotlin
@Composable
fun TactileDeckScreen(
    state: DeckState,
    events: DeckEvents,
    chrome: TopStripInput,     // issue #4's input, §6.3
)
```

`DeckEvents` is one interface (~16 verbs: the slide's arm, STOP's
two tenses, DONE, the two utility doors, the sentence's FIX routing,
dial/blocks setters, struck/sled callbacks) — implemented once by
the VM, faked in one line by render tests. MainActivity's Deck
call-site shrinks from ~55 named arguments to 3. **Enforcement:**
a signature lint in the existing test source set — a unit test
reflecting on the composable's parameter count (or, simpler, a
grep-guard in CI over `TactileDeckScreen(` call-sites) — fails any
merge that grows a fourth parameter. The 21 dead wires died once
by neglect; they stay dead by machinery.

---

## 2 · THE FUSION, MAXIMISED — THE DIAL IS ITS OWN CLOCK

RAHMEN §2 is the floor: hub = the readout, rim ink = progress,
Δ-address = 0, FLOW has no denominator. The MAX layer takes the
sentence *"the dial becomes its own clock"* literally, in four
further steps.

### 2.1 One arc, two tenses — **MAX**

Today the wheel's rim is decoration at idle and is *deleted* at
the seal (the shipped code swaps wheel → 6dp progress track —
the anti-Fusion, in production). Maximised:

- **At idle, the rim arc is the set, drawn.** From the reader
  position, an arc spanning `spanMinutes / 180min` of the dial's
  scale renders in instrument ink at rest weight — the session
  you are about to seal, visible as geometry before it is
  visible as time. Turning the wheel redraws the arc; cycling
  blocks lengthens it *and* cuts its seams (§2.2).
- **At the seal, nothing is swapped.** The same arc, same
  radius, same start angle, steps up one ink tier (the Strike,
  at the seat frame) and becomes the progress arc: its *filled*
  portion is elapsed/target, filling (F3's truth), its unfilled
  portion is the set-arc still standing. Set and progress are
  one object photographed at two moments — that is the Fusion's
  whole claim, made literal.
- **Dies, verifiably:** the hero card and its padding/border/
  shadow question (F11 moot), the eyebrow row, the hero glyph
  composable (all three prints), the 6dp track, the `%` text,
  and the entire `progressFraction` derivation (~25 lines in the
  screen, incl. its floored/invented FLOW branch). If any of the
  six still composes after this lands, the Fusion has not landed
  — RAHMEN's test, kept verbatim.

### 2.2 The arc carries the session's structure — **MAX**

The engine's truth is `n × block + (n−1) × 10`. The arc prints it:

- **Seams:** at each block boundary along the arc, a 2dp gap in
  the ink (Rhea's break-joint insight, from BEYOND F-HUB, moved
  to the rim where it belongs). A 3-block session's arc is three
  runs of ink with two visible joints — the breaks are *in* the
  progress, not a footnote under it.
- **Position in structure:** the hub sub-line during `Running`
  prints `BLOCK 2 OF 3` (Anonymous Pro, quiet) when blocks > 1 —
  derived from `currentBlockIndex`, already on the session
  entity. During `Break` the sub-line is `BACK AT HH:MM` and the
  seam being crossed is the one the ink is sitting in.
- **Cost:** seams are a draw-time mask on the existing arc path
  (segment angles precomputed from block/seam durations — pure
  arithmetic on fields the state already carries). No new
  element, no new data.

### 2.3 The hub — one glyph, every tense

Per RAHMEN §2.1's table, with the mechanism made explicit:

| State | Hub primary (Sora) | Hub sub-line (Anonymous Pro) | Hub gesture |
|---|---|---|---|
| Idle / IdleFault | span `1H 50M` / `FLOW` | recipe `3 × 50` (+ `+10 BREAK` when n>1) | live: press cycles/sets blocks |
| Proving / Jammed | span, frozen | recipe, frozen | dead (locked) |
| Grace | grace countdown `0:07` | `STARTED — STOP IS FREE` | dead |
| Running | remaining `MM:SS` (or elapsed, FLOW) | `BLOCK k OF n` / `NO END` (FLOW) | dead |
| Break | break countdown | `BACK AT HH:MM` | dead |
| FinishDetained | frozen readout | frozen | dead |
| Receipt | — (wheel at rest under the slip) | — | dead |

- **Type:** hub primary at the wheel's own scale — the hub is
  Ø128dp inside a Ø288dp wheel (existing geometry); Sora display
  at ~34–38sp for `MM:SS`/`1H 50M` fits the hub with the sub-line
  beneath at 10sp. This is smaller than today's 72sp hero — and
  that is the honest trade the Fusion makes, stated plainly:
  **the numeral gives up size to gain permanence of address.**
  Mitigation, priced in §7: if device renders fail the 1 AM
  legibility test at hub size, the fallback is RAHMEN's held
  lever in reverse — hub grows by shrinking the tick band
  (Ø128 → Ø152 hub, ticks compressed radially), *not* by
  resurrecting a hero row. The fallback is a geometry change
  inside the one view; the state model does not notice.
- **Single-press (F10)** applies to the hub glyph in its static
  tenses (Idle span) and is OFF on every ticking tense (FLOW,
  Running, Grace, Break) — a press does not strike a moving
  target.
- **Burn-in:** a hub ticking for hours inherits the AOD desk
  clock's shift discipline: the hub *glyph group* (primary +
  sub-line) translates ±2dp on a 60s cycle; wheel geometry,
  arc, and ticks never move. Requirement stated here; mechanism
  is view-side (§4.2) and reuses the desk clock's existing
  shift cadence constant if one exists, else introduces it once,
  shared (§2.5).

### 2.4 FLOW, maximised honestly

FLOW has no end; therefore: no arc fill toward anything, no
`BLOCK k OF n`, no denominator anywhere (F3/T9). What the rim
*can* truthfully do: **tally elapsed minutes** — one minor tick
per elapsed minute steps from rest ink to instrument ink,
monotonically, up to the rim's minor-tick count; beyond that
the tally simply stands complete-looking but semantically
empty, so the tally is capped in print by the hub's count-up,
which is the primary readout. If the merger reads even the
tally as a denominator in disguise, strike it — FLOW stands
complete without it: hub counts up, rim is still, `NO END` in
the sub-line's place. The tally is the only strikeable paragraph
in this section.

### 2.5 **MAX — one clock component, two homes**

The hub readout and the AOD desk clock are the same fact (a
session's elapsed/remaining) at two sizes. Maximised: the
readout's *rendering logic* — glyph formatting, tense rules
(ghosts off while ticking, burn-in shift, single-press at rest)
— is extracted as one component, `SessionClockFace`, consumed
by the wheel's hub (Canvas draw) and by the desk clock
(Composable). Demands: the desk clock's screen adopts the
component's formatting functions (pure Kotlin, shared) even
where its layout stays its own. Gain: a tense rule fixed once
cannot drift between the Deck and the clock it opens — the
exact drift class (Deck vs AOD burn-in) the audit already
caught once (§6, Finish).

---

## 3 · THE 21 DEAD PARAMETERS — EVERY ONE, VERDICT AND DESTINATION

Audit §2's list, verified against the audit's three greps.
Branch `agent/issue-6` wave 1 already cut 20 wires from the
signature (F1, DECLARED), and the cell's closing verdict
corrects the audit's census in one place: the 21st name
(row 21) never existed in this repo — the table keeps all 21
rows so the audit's list is accounted name by name, with the
correction marked where it lands. This table is written against the
audit's original 61-parameter contract so the record is complete,
and §3.1 states what remains live on the branch today. Verdicts:
**DIE** (parameter + host wiring + any VM passthrough deleted),
**MOVE** (the fact/verb's owner screen takes the wire; the Deck's
copy dies), **BECOME** (absorbed as a derivation inside the new
state model — the parameter dies, the fact survives computed).

| # | Parameter | Verdict | Destination / mechanism |
|---|---|---|---|
| 1 | `studyPreset` | **BECOME → then DIE** | Mode is derived: `dial < 10 ⇒ FLOW` (ESR-08 — the engine never read the preset). `SessionSet.mode` computes it; the stored preset field, `onSelectStudyPreset`'s Deck wire, and the FLOW key-cap die together (F2). If the VM keeps a preset for other screens, it is theirs, never the Deck's input again. |
| 2 | `checklist` | **MOVE** | Tasks owns the checklist (it is a task-list fact). Host wires it to the Tasks call-site only. |
| 3 | `completedTasksCount` | **MOVE** | Totals are the Drawer's/Ledger's tenancy (audit §8 evictions). Deck prints no totals in any state — no field in any variant. |
| 4 | `totalQueueMinutes` | **MOVE** | Tasks (queue arithmetic is the sequencer's). |
| 5 | `selectedProfileName` | **MOVE** | Shield — profiles/loadout are already Shield's home (audit §8). |
| 6 | `intentionText` | **DIE** (Deck) | The Deck never rendered it. If the intention feature survives anywhere, its home is the Tasks header; the Deck wire and host argument die regardless. |
| 7 | `totalFocusedMinutes` | **MOVE** | Drawer/Ledger totals. |
| 8 | `totalInterceptions` | **MOVE** | Ledger / PARK warrant detail — machine self-report belongs with the warrant's own board, not the home face (the six-renderings charge, §4 of the audit). |
| 9 | `profiles` | **MOVE** | Shield, with #5. The Deck never listed a profile. |
| 10 | `apps` (list) | **BECOME** | The Deck needs exactly two integers — shut/peek counts — which already exist as `appsSplit`. The list parameter dies; `AppsSplit` becomes a field of `Proving`/`RunningSession`. Shield keeps the list. |
| 11 | `shortsReelsSniperEnabled` | **MOVE** | Shield (its toggle's home). |
| 12 | `browserUrlBlockerEnabled` | **MOVE** | Shield, with #11. |
| 13 | `onToggleChecklistItem` | **MOVE** | Tasks host wiring, with #2. |
| 14 | `onAddChecklistItem` | **MOVE** | Tasks host wiring, with #2. |
| 15 | `onDeferActiveTask` | **MOVE** | Tasks. Deferring is a queue verb; the Deck's running verbs are DONE and STOP only (one question: the task line is a ghost, F7). |
| 16 | `onExtendActiveTaskOvertime` | **MOVE** | Tasks, with #15 — overtime is queue management, never a Deck act. |
| 17 | `onAddQuickTaskToQueue` | **MOVE** | Tasks (quick-add is the sequencer's front door). |
| 18 | `onOpenCurioVault` | **DIE** | A second door to a destination the rail already reaches. Duplicate doors die; the rail is the door (same law as QUEUE's count, F7). |
| 19 | `onIntentionChanged` | **DIE** | With #6. |
| 20 | `onProfileSelected` | **MOVE** | Shield host wiring, with #5/#9. |
| 21 | `onSimulateIntercept` | **ALREADY ABSENT — stays dead** | Cell-corrected (Rost, wave-1 + closing verdict, re-verified at HEAD): this wire **does not exist at this commit** — the audit's census of 21 is 20 verified dead wires + this ghost. No cut is needed or possible. Treatment: the name joins the §8 M1 grep-guard list, so a simulation verb can never be *introduced* at the Deck's host seam; if a harness ever needs one, it lives in the test/debug source set, injected into the harness's fake `DeckEvents`. |

**The pattern the table proves:** of the audit's 21, **zero**
return to the Deck in any form. 3 DIE outright, 1 already
absent (row 21, cell-corrected above), 2 BECOME derivations,
15 MOVE to a named owner — and every MOVE names a screen that
already hosts the fact today, so no destination gains a new
concept, only wires it already conceptually owns.

### 3.1 What remains on the branch, and its fate under §1.5

The branch's post-wave-1 signature (~41 params) still carries
live-but-scattered wires the state model now absorbs:
`queuedTasks` (only its *title-bearing* head survives — as
`ghostTitle`; the list dies with F7), `onToggleTaskSubStep` +
`onOpenTaskSequencer` (MOVE to Tasks with F7 — the ghost has no
tap), `onSelectStudyPreset` (DIE with #1/F2), and the ~14 Slice
D/E seams (`appsCellCount`, `sealFreeze`, `struckSessionId`,
`sealT0MonoMs`, `finishState`, `receiptState`,
`sledInitialFraction`, the fix/retry trio…) which BECOME fields
of `DeckState` variants or verbs on `DeckEvents`. Net:
**61 → ~41 (wave 1) → 3 (this spec).**

---

## 4 · MECHANISM — HOW IT IS BUILT, VIEW BY VIEW

### 4.1 The screen (`TactileDeckScreen.kt`, 1,163 lines today)

Becomes a pure renderer over `DeckState`: an exhaustive `when`
over nine variants composing from a shared kit (wheel, foot
line, ghost row, utilities, pinned stack). Estimated shape:
~450–550 lines — the derivations (~350 lines of the current
body: freeze capture, cell derivations, progress, eyebrow and
hero construction, press-latch plumbing) leave for the VM
combine and the wheel. Scroll column survives only where a
state can overflow (drawer open); `Idle`/`Running` compose
without scroll at 740dp (§5).

### 4.2 The wheel (`RotaryWheelView`, in `BlockitCoreViews.kt`)

The one genuinely restructured object. Changes:

1. **Single render input.** Six mutable vars (`minutesValue`,
   `studyBlocksCount`, `isLockedSession`, `isNightMode`, …)
   become one `render(state: WheelRenderState)` — a plain data
   snapshot (dial, blocks, mode, hub primary/sub-line strings,
   arc: set-span fraction + seams + fill fraction, locked,
   night, glyph-shift phase). The Compose side maps
   `DeckState → WheelRenderState` in ~40 lines of pure code.
   The view keeps its physics (tetrad hub, strait, loaded
   wheel — BEYOND W2, untouched) for the unlocked tenses and
   ignores input in the locked ones, as `isLockedSession`
   already does.
2. **Hub text on Canvas.** `drawText` with the Sora typeface
   (loaded once from the app's font resources — the family
   already ships for Compose; the view takes a `Typeface`
   at construction, supplied by the AndroidView factory) +
   Anonymous Pro for the sub-line. Static tenses draw once per
   state change; ticking tenses invalidate at 1Hz from the
   session flow's seconds (the VM already ticks the session —
   **no new timer, no new alarm**), and only the hub's dirty
   rect is invalidated where the platform allows.
3. **The arc.** One path, three layers drawn in order: rest
   track (full scale, ground ink) → set-arc (instrument ink,
   seam-gapped per §2.2) → fill (hero ink, `elapsed/target`
   of the same path). At idle fill = 0; the seal's Strike is
   the set-arc's ink stepping up at the seat frame — a colour
   change on an existing path, inside the existing 1050ms shot.
4. **Persistence across the seal.** The AndroidView instance
   hoisting the current code already performs (wheel/slide refs
   held across the seal, Slice E) becomes the rule for the
   wheel's whole life: the wheel is composed in **every**
   variant (under the receipt slip in `Receipt`), never
   swapped for a track. The shot (F15 form: ground recedes,
   instrument in focus) animates the *container*, not the
   instrument.

### 4.3 The seal shot — unchanged machinery, new subject

`SealShotState`, the t=0 law, the 1050ms curves, Δy=0: all
kept (acquitted machinery). What changes is only what the shot
acts on: today it freezes a wheel that then vanishes; maximised
it lands the hub's tense change (span → readout, arc → filling)
at the same frames the ground steps (t=400). Input death during
the shot stays exactly as specced (Zug-II §4.6).

### 4.4 The pinned stack and the chair

Slide → STOP chair at one address (F6, RAHMEN §4.4): the zone's
tenant is a function of state — `Idle/IdleFault/Proving/Jammed`
→ slide; `Grace/Running/Break` → the STOP chair filling the
full 72dp, its words carrying the price after grace; the grace
countdown itself is in the hub (§2.3), never on the chair.
`PressLatch` survives as the slide's own mechanism (Emil's
latch is *about the press*, so it stays screen-side, §1.4).

### 4.5 The drawer (F16)

`Proving`/`Jammed` compose the proof drawer in the ghost row's
room (displaced, not stacked; RAHMEN §4.1) from the variant's
`ProofSnapshot` — including the +1 settle beat, which is a
*presentation timer in the VM's derivation* (the combine holds
`Proving` for one beat after SealedRunning before yielding to
`Grace`), so the screen needs no timer of its own. Dens's
Two-Height Strip remains the priced floor if the drawer is
refused at merge (§7).

### 4.6 Accessibility (F22, priced into the Fusion, not after it)

Fusion concentrates control + readout in one view, so its
semantics are one larger obligation (RAHMEN §8): the wheel
exposes a single accessibility node — name ("Session dial"),
value (the hub's exact print, per state), actions (rotate ±5,
cycle blocks, and in running tenses the node is a live-region
readout). Building it *with* the hub text (§4.2.2) costs a
fraction of retrofitting it; §7 prices it inside the Fusion
lane, and no merge may call the Deck finished while it stands
(audit F22, binding).

---

## 5 · THE CHROME COLLAPSE — 43%, ACCOUNTED HONESTLY

Audit §4: permanent chrome = **316.5dp** of a 740dp phone =
**43%** — header 48 + pinned stack 216.5 + rail 52; 61.5dp of it
reserved emptiness. The maximised Deck's accounting:

| Line | Today | Fused Deck | Whose move |
|---|---|---|---|
| Header band | 48 | 48 | Host — **not claimed** |
| Rail | 52 | 52 | Host — **not claimed** |
| Proof strip (pinned) | 53 | **0** | Deck: drawer is state-only (§4.5); the strip's successor surface is #4's input (§6.3), not Deck-pinned |
| Slide / STOP zone | 72 | 72 | Kept — the Deck's one verb earns its room |
| Refusal sentence slot | 22 reserved-empty | **0 reserved**; +22 in `IdleFault`/`Jammed` only | Deck: fault grows, health doesn't pay rent |
| Imprint | ~39.5 | ~39.5 | Kept — the seal's fixed record |
| Pinned spacing | ~30 (in the 216.5) | ~8 | Deck |
| **Deck-owned pinned total** | **~216.5** | **~119.5** | **−97dp returned** |
| Hero card (scroll column) | ~470 (card + eyebrow + 72sp glyph ×3 + 288 wheel + FLOW row / track + %) | **288 + foot line ~20** | Deck: card, eyebrow, hero row, track, % all die into the hub (§2.1) |

**Result, stated without inflation:** total permanent chrome
316.5 → **219.5dp ≈ 30%** of 740dp — and the remaining 100dp of
that is host chrome this spec does not touch. The Deck-owned
share falls 216.5 → 119.5 (**−45%**), reserved emptiness falls
61.5 → **0dp**, and the idle face fits whole at 740dp with the
wheel at full 288dp: column = 740 − 48 − 52 − S(strip input) −
119.5 − wheel 288 − foot 20 − ghost 24 − spacing ~40 ≈
**148dp of air at S=0** — air, not furniture (RAHMEN §1's law:
returned dp is spent on fit, then silence, then nothing).

The six renderings of one fact (audit §4) fall to: header Guard
line (host, PARK's door) + imprint tenant (the Deck's one) +
drawer cells (state-gated) = **one Deck rendering, in the
states whose question it is** — enforced by §1.2's field sets,
not by a comment celebrating a law (audit §7's meta-charge).

---

## 6 · DEMANDS ON THE APP

### 6.1 Demands on the ViewModel / engine

- The `deckState` combine (§1.4) and the death of the stored
  preset's Deck wire (§3 #1). **No engine change:** the cascade,
  t=0, seal freeze, finish machine, receipt — all consumed as
  existing flows. Block cap 8 / tetrad behaviour: already the
  wheel's shipped grammar.
- The one-beat drawer hold (§4.5) is a derivation detail in the
  combine, not a new timer service.
- **Database: NO migration.** Span, stored end, block index,
  held-at-seal counts — every field §1.2 names already exists
  in the v11 schema. This design is DB-neutral by construction;
  any builder draft that adds a column has misread the spec.

### 6.2 Demands on the other destinations

- **Tasks** absorbs: checklist wires, queue minutes, defer,
  overtime, quick-add, sub-step toggling, the sequencer door
  (its own door — it was always Tasks opening itself). Net
  effect on Tasks: host call-site grows; no new concepts.
- **Shield** absorbs: profiles list + selection + name, the
  two toggles. Already its facts; only wiring moves.
- **Drawer/Ledger** absorbs: the three totals (completed
  count, focused minutes, interceptions).
- **PARK** receives the seam stamps (F8) — host chrome's
  business, noted here only because the Deck stops printing
  them.
- **Desk clock** shares `SessionClockFace` formatting (§2.5).
- Cross-destination flows stop being collected for the Deck's
  sake: MainActivity's Deck call-site no longer forces
  profiles/apps/checklist collection on Deck entry — a small,
  real startup/egress win, free with §1.5.

### 6.3 The input contract owed by issue #4's cell

The Deck consumes the top strip as `TopStripInput(heightDp: S,
warrantPrintState)` — nothing else. In return the Deck
guarantees: no Deck element duplicates a fact the strip
prints, no Deck layout constant assumes `S`, and cover-the-
strip (RAHMEN T1) passes. What the strip *is* — its height,
words, the SealBar's 60dp housing on current main — is #4's
ruling; if `S` grows, the Deck's compression ladder (RAHMEN
§7) absorbs it before the wheel is touched. One coordination
note, offered not imposed: the Deck's `sealBarStateFor`
derivation currently lives screen-side; under §1.4 it moves
into the VM combine, and #4's cell should consume it from
there rather than re-deriving — one derivation, two consumers.

### 6.4 Design law, complied

Fonts: Sora (hub primary, display) · Atkinson Hyperlegible
(ghost title, sentence, chair words) · Anonymous Pro (sub-
lines, recipe, foot line, telemetry). **No green or near-green
anywhere** — the arc's inks are scheme tokens (hero / instrument
/ rest), never a status colour; fault is carried by rank and
room (§1.3), not by hue. No AI imagery — the instrument is
typography and geometry only. Token sweep (F17) precedes any
look touching this screen, with RAHMEN §6's four-rank token
vocabulary (heroGlyph / instrument / control / ground + the
single ghost token) as its required output for the Deck.

---

## 7 · COST — PRICED PER PIECE

S = a sitting's edit · M = a lane · L = a restructure.
Line estimates are net deltas against current main, from the
file sizes in §4; they are estimates, labelled as such.

| Piece | Price | Est. lines | Notes |
|---|---|---|---|
| State model: `DeckState` + VM combine + precedence (§1) | **L** | +~350 / −~350 (screen derivations move, not vanish) | The restructure everything else hangs on |
| Host collapse 61 → 3 + signature lint (§1.5, §3) | **M** | −~120 host/screen; +~80 `DeckEvents` | Includes all DIE/MOVE wiring in MainActivity |
| Fusion core: hub text + one-arc-two-tenses + lock-in-place + kill card/track/% (§2.1, §4.2) | **L** | wheel +~400; screen −~200 | The instrument restructure; tetrad/strait physics untouched |
| Arc seams + `BLOCK k OF n` (§2.2) | **S** | +~60 | Pure draw-mask + one derivation |
| FLOW tally (§2.4) | **S** | +~30 | Strikeable; lands only if the merger acquits it |
| `SessionClockFace` extraction (§2.5) | **M** | +~120 shared; desk clock −~40 | Pays for itself at the first tense-rule fix |
| Drawer + settle beat (§4.5, F16) | **M** | +~180 | Dens Two-Height floor if refused: **S–M**, and the collapse in §5 loses ~30dp of its claim |
| STOP chair unification (F6) | **M** | ~±120 | Audit-priced M; chair words + full-72dp fill |
| Ghost task line (F7) | **S** (post-state-model) | −~90 | Was M on the flag model; the variant field-set does most of it |
| Accessibility node for the wheel (F22, §4.6) | **M** | +~120 | Inside the Fusion lane's definition of done |
| Tests: 9-state render set, T1–T10 (RAHMEN §9) + precedence table tests + one-numeral grep guard | **M** | +~400 test | The precedence table (§1.4) is written to be tested row by row |

**Headline: 2 L + 4 M + 2 S — two lanes** (Lane A: state model
+ host collapse, lands first; Lane B: Fusion + chair + drawer,
lands on A), plus the test lane riding B. No DB migration, no
new dependency, no machinery change (cascade/warrant/seal/
finish untouched), no other screen's composition altered —
Tasks/Shield/Drawer change only in host wiring. Sequencing
law (RAHMEN §8, kept): the interim S-fixes (F3/F4/F9/F10/F12–
F14/F19/F21) may land on the current room ahead of Lane A;
F17's token sweep precedes any look; the 288→230 shrink stays
held, unspent, behind the compression ladder.

**Risks, named:** (1) Hub-size legibility (§2.3) — the one
genuine unknown; gated by device renders + the 1 AM test, with
the Ø152-hub fallback priced as S inside Lane B if triggered.
(2) Canvas text in the wheel is the only new rendering
technique; if Sora's typeface cannot be supplied to the view
cleanly, fallback is a Compose overlay positioned on the hub —
same state model, +S, and the overlay must still die with the
view in `Receipt`. (3) The combine's precedence table is the
single place a state bug can live — which is precisely its
virtue: nine rows, all testable, no fourteenth flag.

---

## 8 · ACCEPTANCE — WHAT "MAXIMUM" MUST PROVE

RAHMEN's T1–T10 stand, plus, for this layer:

- **M1 · Parameter census:** the Deck composable's signature
  is 3 parameters; a grep over the 21 names of §3 finds zero
  occurrences in the Deck screen and its MainActivity call-site.
- **M2 · Unrepresentable clutter:** for each non-`Proving`/
  `Jammed` variant, the proof cells compose zero times; for
  `Idle`, BLOCK prints zero times — asserted by render tests
  per variant, not by review.
- **M3 · One clock:** exactly one component in the tree renders
  a session readout on the Deck (the hub); the desk clock
  renders the same formatting component (§2.5).
- **M4 · Arc continuity:** idle frame's set-arc geometry
  (radius, start angle, span fraction, seam positions) equals
  the running frame's progress-arc geometry for the same set —
  screenshot-diffable.
- **M5 · Chrome receipt:** pinned Deck-owned height measured
  on-device ≤ 120dp in `Idle`/`Running`; `IdleFault` ≥ +20dp
  over it (fault grows); reserved-empty height = 0 in all states.
- **M6 · Precedence:** the §1.4 table, one test per row,
  including routine-born entry at rows 5–7 with no shot.

---

*What I want to do next: the same maximum pass over the drawer's
interior mechanics — the three cells as one `ProofSnapshot`
renderer with its settle-beat timing made VM-testable — the
moment Lane A's state model is ruled.*
*What I could do unassigned: a census script (grep-guard) that
fails CI when any of §3's 21 names reappears at a Deck call-site,
so F1's cut can never silently regrow.*

— MIP-03 (Maximum-Implementation-Potential, issue #6)
