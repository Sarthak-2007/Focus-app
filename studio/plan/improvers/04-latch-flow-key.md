# PLAN — Latch (Improvers), FLOW key, Deck
## Wave: PLAN (documents only, no code) · Coordinator: Ilya · Oct 29, 2026

Element: the `FLOW` key, footer-left of the Deck instrument panel, opposite its own
consequence text (`FINISHES …` / `OPEN FLOW • 20M FLOOR`).
Home: `TactileDeckScreen.kt:259–282` (idle branch only). Helpers `inkControlFill()` /
`onInkControlFill()` in `MonolithCustomComponents.kt:439–451`. Curves in
`theme/MonolithCurves.kt`. Sound/haptics in `service/TactileSoundEngine.kt`.

---

## CURRENT — read from today's code, not from wave-1

**Did the skirt/sink ship? No.** Not one millimetre of it. The key in today's tree is
byte-for-byte the object wave-1 indicted: one flat `Box` —
`.clip(8dp) → .background → .border(1dp) → .clickable → .padding(10dp H, 5dp V)` —
no well, no skirt, no offset, no animation, no `interactionSource`. The synthesis
craft call ("FLOW keeps, skirt travel + sunken ON state") is a decision on paper
that never became code. Renders agree: in `shots/01_deck_view.jpg` /
`shots/night/n01_deck_view.jpg` it is the smallest, thinnest object on the panel —
a ~42 × 26dp outlined tag whose legend reads lighter than the `FINISHES` text beside
it. The code comment still claims "a raised pill … reads pressable." It does not.

**What changed underneath it since wave-1 (wave-1's tables are stale in two places):**

1. **The night staircase was rebuilt.** Wave-1 specced against night
   `surfaceVariant #272320` (above the panel — depth sign flipped at night) and
   `onBackground #F8F5F5`. Current `Color.kt`: night ground/panel `#171412`,
   `surfaceVariant #100E0C` (now genuinely *below* ground), `surfaceBright #362C27`,
   `onBackground #D2C6BB`. Consequence for this key, today: day OFF fill `#D8D1CB`
   sits one step *below* the `#E4DFDA` panel; night OFF fill `#100E0C` also sits
   below the `#171412` panel — the flip is gone, OFF now reads recessed in both
   modes. But ON is worse than wave-1 knew: night ON fill `inkControlFill #4A423C`
   is the *lightest* mass in the footer — the engaged key pops *up* off the panel
   while the disengaged key sinks. The depth story is now consistently backwards
   for a latch: rest = down, latched = up. Exactly inverted from the synthesis call.
2. **The fonts are real now.** Anonymous Pro Bold is a true 700 file (Round-0 fix);
   wave-1's "synthesised smear" caveat no longer applies to this legend. The 11sp
   Bold still *reads* thin in renders — that's the face at 11sp, not a fake weight.

**The feedback lie — wave-1 said "no haptic, no sound." At the view layer, true.
At the system layer, false, and worse than silence:** the click handler calls two
setters back to back — `onSelectStudyPreset(STOPWATCH_FLOW)` then
`onDialMinutesChanged(5)`. In the ViewModel, `selectStudyPreset`
(`MonolithViewModel.kt:382`) plays `playMechanicalLatch(true)` *and* resets dial
to 5 *and* resets blocks to 1; `setDialMinutes(5)` then plays a rotary detent if
the value changed. So one tap on the key is voiced **twice, by two different
mechanisms, owned by nobody** — a latch thock plus a wheel tick for a wheel the
finger never touched. And tapping the key while it is already ON replays the
*engaged* latch sound: the machine says "engaging" for a tap that changes nothing.
Feedback exists; it is mis-owned, doubled, and it lies.

**The state has three authors, and they can disagree (the real defect):**

- `TactileDeckScreen.kt:116–120`: UI flow = `preset == STOPWATCH_FLOW || dial < 10`.
- `MonolithRepository.kt:876`: the *session's* flow = `durationMinutes < 10` —
  the preset is never consulted at arm time.
- The slide badge: `dialMinutes < 10` again — a third reading, in the same
  screen as the first.

**And the preset half of the fork is permanent** (Moss, Critics, by return
mail — verified against the tree while this plan was being written): the FLOW
key at `TactileDeckScreen.kt:267` is the **only caller of
`selectStudyPreset` in the entire app**. No surface anywhere ever selects
another preset. So one tap sets the preset to FLOW *for the life of the
ViewModel*, and from that moment the divergence is not an edge case, it is
the steady state: drag the wheel back to 50 and the Deck's readout branch
(preset-driven) renders `NO LIMIT` and `OPEN FLOW • 20M FLOOR` — while the
slide badge (dial-driven) says `50M` and the repository (dial-driven) arms
an honest timed 50-minute session underneath the FLOW skin. A lying screen,
one tap from the centre gesture, with **no exit at all** short of killing
the app. Re-dialling does not escape it; nothing in the UI can.

**The trap, priced:** that same tap also silently destroys the current
setup — dial (e.g. 50) → 5, blocks (e.g. 3) → 1 (`selectStudyPreset`
applies the preset's `defaultBlocks`, unrecorded). There is no unlatch on
the key, no restore anywhere, and per the paragraph above, not even a
cosmetic recovery. The key is a one-way door that eats your settings on
the way through and then stays visibly open forever.

**What FLOW actually arms (this is the session the key sells):** an open stopwatch.
Repository target = `STOPWATCH_MIN_FLOOR_SECONDS` = 1200s (20-min floor), no finish
time; readout flips to elapsed, caption `NO LIMIT`; slide badge flips `25M` →
`FLOW`. Exit economics differ from a timed session: after the 20-min floor,
ending is a clean completion that spends **no** emergency token; before the floor,
exit routes to the 60s-hold disarm challenge. Blocks are forced to 1. All of that
is decided by this one 26dp-tall tag.

**What is already right and stays:** footer-left placement opposite its consequence
text; idle-only existence (the running branch replaces the whole row with the
progress track — the key cannot be touched mid-session, and no rung below changes
that); Anonymous Pro legend; ON = ink fill family, not accent (but see CONFLICTS).

| Property today | Value |
|---|---|
| Touch target | ≈42 × 26dp (the visual box *is* the target) |
| Radius | 8dp (matches the synthesis freeze: 8dp keys / 16dp panels) |
| Type | Anonymous Pro, real Bold, 11sp, letterSpacing 1sp |
| OFF | day `#D8D1CB` on panel `#E4DFDA` · night `#100E0C` on panel `#171412` — recessed both |
| ON | day ink `#2C2A29` / text `#FBF1EC` · night `#4A423C` / text `#FFE4CF` — raised both |
| State cues | fill value swap only. No position, silhouette, marker, or semantics (`Role.Switch` absent; a screen reader hears a button that never says its state) |
| Press | none — no down-state of any kind; the global indication is a purged no-op, so the tap is answered only by other parts of the screen changing |

---

## LADDER — 4 rungs, each shippable alone, each in order worth more than the last

Shared vocabulary: curves are the app's own — A `cubic-bezier(0.2, 0, 0, 1)`
(`EasingA`), B `cubic-bezier(0.05, 0.7, 0.1, 1)` (`EasingB`), overshoot
`MechanicalOvershoot(1.25)`. Travel is always illusion: vertical offset + skirt
band + highlight change. No elevation, no drop shadow, at any rung. Haptics are
`VibrationEffect` waveforms in the engine's existing family; amplitudes 0–255.
All colour from existing tokens; nothing green anywhere (every token below
satisfies the house zero-green rule).

### Rung 1 — A toggle that tells one truth *(behaviour first; ~1 VM function + handler rewire)*

Before any paint: make the key honest, reversible, and single-voiced. The flat
chip survives this rung visually — what changes is that it stops lying.

- **Single entry point:** one ViewModel call, `toggleFlow()` — which also
  gives the preset its first-ever *second* caller and the app its first
  exit from FLOW. Engaging captures `{preset, dialMinutes}` into a restore
  slot, then sets preset `STOPWATCH_FLOW`, dial 5. Disengaging restores
  both atomically. **Blocks are never touched** (Moss's condition, adopted):
  the timed blocks value is *suspended, not reset* — a flow session arms
  with 1 block explicitly at session creation, the hub renders its count
  only in timed mode, and the user's 3× is sitting exactly where he left
  it when he comes back. No restore slot for blocks because nothing was
  taken. The composable's two-setter click dies; the key never calls
  `setDialMinutes` again, so the phantom wheel-detent under a key tap
  dies with it.
- **Single voice:** exactly one sound per tap, from the engine's existing
  `playMechanicalLatch(engaged)` — waveform as shipped (engage: timings
  `[0, 11, 26, 9]ms`, amplitudes `[0, 215, 0, 150]`, 760Hz / 16ms / gain 0.54;
  release mirrored at 520Hz). Tapping an ON key now plays the *release* shape,
  because it releases. No new audio code at this rung.
- **Semantics:** `Modifier.toggleable(value = flowOn, role = Role.Switch)`,
  `contentDescription = "Flow — open-ended stopwatch"`, `stateDescription =
  "Flow on" / "Flow off"`.
- **Target:** touch box grows to **64 × 48dp** (visual chip unchanged, centred) —
  the 26dp target was half the floor and sits on the arm path of a tired user.
- **Press answer (the minimum):** on DOWN the chip translates **+1dp**, 90ms
  Curve A, returns on release/cancel; state flips on DOWN, not on click-up —
  the house grammar is that controls answer on down and state words flip on
  arrival. Fill crossfade on toggle 120ms Curve A.
- **Depth/tokens at this rung:** unchanged from CURRENT (flat, recessed OFF /
  ink ON). Stated plainly so nobody mistakes this rung for the depth fix.
- **Cost to ARM:** zero added steps; a mistaken tap now costs one tap (~0.3s)
  to undo instead of a re-dial + re-cycle (3–8s). This rung alone removes the
  only way the key could blow the ≤10s arm budget.

### Rung 2 — The well, the skirt, the sink ★ *(the depth decision; contains ★)*

**Decision: OFF = cap raised in a well. ON = cap sunk to the well floor.**
A latched key stays down; a key standing proud is a key at rest. Timed is the
default, so the raised cap is the invitation; once chosen it should sit *in*
the panel, quiet — and height is the one cue that survives grayscale,
peripheral vision, and a 25%-scale glance.

- **Geometry:** touch box **68 × 48dp**; well **60 × 38dp**, radius **10dp**,
  centred; cap **54 × 30dp**, radius **8dp** (well = cap + 2dp reveal,
  concentric). Cap rest: OFF `offsetY 0dp`, skirt **3dp** visible below the
  face; ON `offsetY +2dp`, skirt **1dp**. Total travel **2dp**.
- **Depth recipe:** well = one tonal step *below* panel in both modes, closed
  by a 1dp shadow line on its top inner edge and a 1dp light-catch on its
  bottom inner edge; no outer border — the value step is the edge. OFF cap
  face = one step *above* panel: a symmetric staircase well −1 / panel 0 /
  cap +1. ON cap drops to well level; its seam against the well reads as a
  gap. Identical construction day and night — the new night tokens finally
  allow this (`#100E0C` is a true below-ground recess now).
- **Colour (current tokens):**

  | Part | Day | Night |
  |---|---|---|
  | Well fill | `#D8D1CB` surfaceVariant | `#100E0C` Night surfaceVariant |
  | Well top inner line 1dp | outlineVariant `#75615B5A` | black @ 40% |
  | Well bottom inner line 1dp | `#FAF6F0` surfaceBright @ 50% | `#362C27` Night surfaceBright |
  | OFF cap face | `#FAF6F0` surfaceBright | `#362C27` Night surfaceBright |
  | OFF cap skirt 3dp | `#D8D1CB` surfaceVariant | `#161311` NightBlockBarSurface |
  | OFF legend | `#2C2A29` | `#D2C6BB` |
  | ON cap face | `#2C2A29` inkControlFill | `#4A423C` inkControlFill |
  | ON cap skirt 1dp | `#2E2A26` TokenBlockBarSurface | `#161311` NightBlockBarSurface |
  | ON legend | `#FBF1EC` | `#FFE4CF` |
  | ON marker line 10 × 2dp | `#FBF1EC` | `#FFE4CF` |
  | Cap edge 1dp, OFF only | outlineVariant | Night outlineVariant `#758F8888` |

  Note the deliberate inversion this rung performs at night: today's ON
  (`#4A423C`, lightest in the footer) keeps its fill but loses its height —
  sunk, seamed, skirtless, it reads *held down* despite being the lighter
  tone, because position and silhouette outrank value. The desat test in
  Rung 3 is the check.
- **Marker (state without colour):** a **10 × 2dp** line centred under the
  legend, present only ON; its layout slot exists in both states so the
  legend never jumps. Legend Anonymous Pro Bold **11.5sp**, letterSpacing
  **1.25sp**.
- **Motion:** sink **90ms Curve A**; settle (skirt + highlight finish) **160ms
  Curve A**; rise **180ms `MechanicalOvershoot(1.25)`** capped at **+0.5dp**
  past rest — a key popping off its latch may overshoot a hair; a key going
  down may not. Fill crossfade 120ms Curve A, synced to travel.
- **Haptic (dedicated, replaces Rung 1's borrowed latch):** latch waveform
  `createWaveform([5, 25, 9]ms, [150, 0, 210])` — contact, travel gap,
  bottom-out. Unlatch `[9, 20, 5]ms @ [170, 0, 130]` — release clack first,
  softer settle. Both quieter than the slide's seal: a mode key never
  out-thocks the commit.
- **Sound:** still silent, deliberately, one more rung — the haptic carries
  the detent; the pitch pair in Rung 3 must be tuned against the wheel's
  detents on a device, and a guessed pitch shipped here spends the budget.

### Rung 3 — The crown and the voice *(finish the physical object)*

- **Crown:** the OFF cap face becomes a two-stop vertical gradient of existing
  tokens only — day `#FAF6F0 → #E4DFDA` (surfaceBright → panel), night
  `#362C27 → #100E0C` (Night surfaceBright → Night surfaceVariant) — a ~1dp
  crown drawn as light, not a bevel. ON removes the gradient entirely: a sunk
  key catches no crown light. Pressed transient: **+1dp** beyond target rest
  and crown alpha → 30%, both directions (pushing a latched key deeper before
  it releases is what the object does).
- **Final geometry nudge:** touch **72 × 48dp**, well **62 × 40dp** r11, cap
  **56 × 32dp** r8; travel grows to **3dp** (OFF `−1dp` proud of the well rim,
  ON `+2dp`). Marker becomes **12 × 2dp**, inset 4dp below the cap's top edge,
  revealed with a 40ms-delayed fade + 1dp downward slide as the cap sinks —
  it reads as uncovered by the sinking. Legend **12sp**, letterSpacing
  **1.5sp** (≈33.5dp of glyph in a 56dp cap, ≥11dp side margins).
- **Motion final:** press-in **80ms Curve A** (keys bottom out fast; slower
  reads as a button animating); latch settle **140ms Curve A**; unlatch rise
  **200ms overshoot** as Rung 2; no scale, ever.
- **Sound final (the pitch pair is a state channel):** through the same
  transient synth family as wheel and slide. Latch-in: **190Hz, 40ms,
  gain 0.5** — low thunk, the key bottoming in its well. Unlatch: **330Hz,
  28ms, gain 0.4** — higher, shorter release click. Down-and-low = engaged,
  up-and-high = released, audible eyes-off. Sound toggle off → the haptic
  pair carries the same distinction alone; sound confirms, never solely
  carries. Haptics as Rung 2.
- **Focus:** keyboard/switch-access focus = 1dp ink outline offset 2dp
  *outside the well* — the only outline this element ever draws outside its
  own geometry (day `#2C2A29`, night `#FFE4CF`).
- **Verification, stated so it can be run:** screenshot both states,
  desaturate, shrink to 25%, blur 2px — states must still separate on
  position (3dp), silhouette (skirt 3→1dp), and the marker line alone.

### Rung 4 — One author, whole panel *(past wave-1: kill the other two authors)*

Wave-1 fixed the key and left the state machine forked. This rung unifies it.

- **One flag:** a single `flowEngaged` boolean is the only source. The UI,
  the slide badge, the readout, and the repository all read it.
  `startSession` takes it explicitly; the `durationMinutes < 10` inference
  at `MonolithRepository.kt:876` is deleted. The wheel can no longer mint a
  flow session by drifting under 10, and the preset can no longer claim
  FLOW while the screen shows timed.
- **Wheel floor:** in timed mode the wheel's scale bottoms at **10 min** —
  its bottom detent *is* the key's doorstep. Dragging into the bottom stop
  and holding the overscroll for **250ms** engages FLOW: the key sinks in
  sync with the wheel's end-stop knock (one gesture heard twice, hand and
  eye). **Second exit, adopted from Moss:** while FLOW is engaged, dragging
  the wheel to ≥10 is a disengage gesture — it writes the same flag off
  and takes the dragged value as the new dial (no restore; the hand has
  explicitly chosen a new duration). Key tap and wheel drag are two
  *inputs* to the one flag, never two authors of it. Disengaging via the
  key restores per Rung 1 — and the restore is *visible*: the wheel sweeps
  back to the restored dial over **260ms Curve B** while the readout
  counts back and the key rises on its 200ms overshoot. The whole
  instrument reverses, not just the tag.
  *Where I decline Moss's table:* "derive Deck mode from the dial alone"
  makes a drag accident a mode change and leaves the key as pure
  decoration on a threshold — the fork he killed, inverted. One flag,
  written deliberately (tap, or a 250ms held overscroll, or a drag out),
  read by everyone: that is his no-lying-screen demand met without
  handing authorship to momentum.
- **Slide contract (mailed to Zug-II, see CONFLICTS):** engaging FLOW changes
  the slide's *payload and badge only* — badge `FLOW`, session = open
  stopwatch, 20-min floor, blocks 1. The gesture, threshold, dwell, and seal
  sound/haptic are **identical** in both modes. A tired thumb must never
  have to learn two commits.
- **Depth/tokens/haptic/sound:** unchanged from Rung 3 on the key itself;
  the wheel sweep reuses the wheel's own detent voicing (its owner's spec),
  no new sound is minted here.

---

## ★ BEST-RATIO CHANGE

**★ The collapsing skirt: a 3dp bottom band in the next tonal step down that
shrinks to 1dp as the cap drops 2dp when FLOW latches — and reverses on
unlatch (Rung 2's mechanism, shippable without the well if it must be).**

Priced: one nested bottom band + one `animateDpAsState` driving offset and
band height, **~15 lines** inside the existing `Box`, **zero new tokens,
zero new colours**, no accent spent. It is the only single commit that
simultaneously (1) makes the "raised pill" comment true — a thing with a
visible side wall is a thing that can go down; (2) creates the press
affordance the key has never had; (3) delivers state-as-position — latched
= sunk, readable in grayscale, at a glance, identically day and night on
the *new* night tokens. One condition, priced in: it must not ship before
Rung 1's toggle — a key that sinks beautifully and can never rise is a
better-decorated trap.

---

## PEAK (≤8 lines)

A 56 × 32dp cap in a 62 × 40dp well: raised and crown-lit when timed is the mode,
sunk 3dp, skirt collapsed, flat ink, marker line uncovered when FLOW is latched.
One tap engages — dial captured, blocks untouched — one tap or a wheel-drag
out restores it, the wheel visibly sweeping home. One flag authors the state; key, readout, badge, and
repository all read it. Low thunk down (190Hz), high click up (330Hz); the haptic
pair says the same with sound off. No orange, no decisions, no surface mid-session:
the key exists only before the slide, and the slide seals whatever it says.

---

## STANDARDS CHECK

- **ARM ≤ 10s — PASS, and the key is a shortcut, not a step.** FLOW is a
  parallel alternative to wheel-dragging: one tap (~0.3s) replaces a dial
  drag, adds zero confirmations, zero screens. The slide that follows is
  the identical gesture in both modes (Rung 4 contract). The only ARM risk
  the key ever posed was the *mistake cost* — today's trap re-priced at
  3–8s of re-dialling; Rung 1's capture/restore cuts recovery to one tap.
  Worst-case path with a fumbled FLOW tap: tap, tap, slide ≈ 2s of the
  10s budget.
- **HOLD absolute — PASS, structurally.** The key is mounted only in the
  idle branch; the running branch replaces its row with the progress track
  (verified in code today). No rung adds a mid-session FLOW surface, a
  mode switch, or a new exit. FLOW's different exit economics (clean
  completion after the 20-min floor, hold-valve before it) are the
  session's existing rules, untouched — and the hold itself is banked per
  his decision; nothing here re-opens it.
- **TRUTH once — PASS after Rung 4; FAIL today, named.** Today the fact
  "flow is on" is rendered five ways (key fill, `NO LIMIT`, `OPEN FLOW •
  20M FLOOR`, slide badge, hub state) and authored three ways (preset,
  dial threshold, repository threshold) that demonstrably diverge. The
  ladder's end state: one flag; the key's *silhouette* is the primary
  telling (position + skirt + marker, desat-tested), the words at the
  margins confirm what position already said, and the repository obeys
  the same flag the finger pressed. Human language throughout: `NO LIMIT`
  and `20M FLOOR` already speak plainly and stay.
- **His picks — PASS.** Legend in Anonymous Pro at real Bold weight
  (12sp at peak); all fills from the warm token set; nothing green or
  near it at any rung; no new colour enters the palette for this key.

---

## CONFLICTS (+ mail sent)

1. **Orange — argued, and declined, with a priced reserve.** The temptation:
   FLOW-armed is the riskiest session in the app (no finish time), and
   orange would shout it. The refusal: orange on this app is a verb — the
   commit — and the Deck already spends its one orange on the slide cap
   below; an orange *state* on the key teaches the eye that orange means
   "a mode," and the slide stops meaning "the act." Silhouette + `NO
   LIMIT` + badge already triple-carry the state. **Reserve, for
   arbitration by Ilya/Ren:** if the Rung 3 desat test fails on a real
   device, the fallback spend is the marker line alone in accent (day
   `#F55314` / night `#F47442`, 12 × 2dp) — the smallest possible orange,
   on the cap, never the fill. I do not ship it without that ruling.
2. **Radius bookkeeping:** `Theme.kt` still documents 12/24dp shapes while
   the synthesis froze 8dp keys / 16dp panels. The key's 8dp aligns with
   the freeze; flagging the stale comment for whoever owns Theme — not
   this key's fight, but the next reader will trip on it as wave-1 did.
3. **Rung 4 trespasses twice, deliberately and openly:** the wheel floor +
   overscroll-engage touches Rad's wheel, and deleting the repository's
   duration inference touches the session layer. Both are required by
   TRUTH-once; neither is mine to land silently. Routed via Ilya.
4. **Mail sent:**
   - **To Zug-II** (`mail/zug-ii/0519__from-latch__flow-slide-contract.md`):
     the slide contract — FLOW changes your badge word and payload only;
     gesture, threshold, dwell, and seal stay identical; asked him to
     confirm the badge flips on the *flag* (Rung 4), not on `dial < 10`,
     so the slide never announces FLOW for a session the repository
     would time.
   - **To Rad** (`mail/rad/0519__from-latch__wheel-floor.md`): the Rung 4
     wheel floor (timed scale bottoms at 10; 250ms bottom-stop overscroll
     engages FLOW) — flagged as a proposal against his element, his
     detent voicing reused, nothing specced *for* his wheel beyond the
     floor and the knock-sync ask.
   - **Inbox, before finishing:** one letter waiting — **Moss (Critics),
     `flow-key-trap`** — read, verified against the tree (the key *is*
     the only `selectStudyPreset` caller; the preset fork *is*
     permanent), and adopted into CURRENT + Rungs 1/4 above: reversible
     from the same surface ✓, blocks never reset ✓, wheel ≥10 as a
     second exit ✓; his "derive mode from the dial alone" declined with
     reasons in Rung 4. Reply sent to `mail/moss/` under the same
     thread; his letter filed to `mail/latch/read/`.

— Latch, Improvers, FLOW key
