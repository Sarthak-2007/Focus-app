# 08 — Schlitten (Improvers) · App sled rows · PLAN WAVE

Owner: Schlitten, Improvers. Element: `AppRowSledToggleView` rows in Shield §5
(`BlockitSuitcaseAndSwitchesViews.kt`, hosted 54dp in `ShieldMatrixScreen.kt`).
Plan only — no code. Wave-1 ladder is the floor; this starts from what Round 0
actually shipped, verified in code on Oct 29, not from what wave-1 assumed.

## CURRENT — verified in code, Oct 29

**Round 0 ★ — SHIPPED, confirmed.** `onDraw` derives `capTier =
round(rockerIndex)`; cap fill and selected-label colour key to the animated
position, not `currentTier`. Night SHUT re-paired: fill `tokControlFill()`
#4A423C, label `tokOnControlFill()` #FFE4CF cream. Borrowed nav peach #FFB38F
evicted from the night PEEK cap. Night staircase also landed underneath me:
track `tokSurfaceVariant()` is now recess #100E0C (below ground #171412, was
#272320 above it), unselected labels now `tokTextSecondary()` night #9C8E84
(wave-1's flat #F1E8E6 hierarchy is already fixed by tokens — I claim no rung
for it).

**Round 0's side effect — the new top defect.** Night PEEK cap =
`if (isNightMode) tokControlFill()` — the *same* #4A423C as SHUT, same cream
label. At night the material channel is dead: SHUT and PEEK differ only by
position + word. Position does carry it (no lie), but the cap — the thing the
hand throws — is one slug for two different-weight decisions, and in
grayscale / peripheral scan the list's middle tier vanishes.

**Still open from wave-1 (unchanged in code):**
- No first-bind snap: `rockerIndex` inits 0f, the tier setter always animates
  260ms — rows visibly throw from SHUT on load and on LazyColumn rebind.
  Motion the user never asked for, i.e. motion that means nothing.
- Touch is UP-only tap-by-thirds; no drag, no DOWN state. Name-area tap still
  cycles LOCKED→PEEK→OPEN→LOCKED, including the OPEN→SHUT wrap — the most
  consequential change on the row on its least deliberate gesture.
- No-op tap on the seated stop still fires `fireFeel(8L)` and re-invokes the
  callback. One feel for all tiers: `playFirmClick` waveform
  [0,13,20,8]ms @ [0,225,0,135], synth 620Hz/12ms/gain 0.5, chord 1.1.
  `fireFeel`'s own contract note says pitched variants are deferred to a feel
  pass — this plan is that pass for sleds.
- Pip still binary: filled `tokTextPrimary()` if SHUT, `tokOutline()` for
  both PEEK and OPEN, r=3dp at x=6dp. Name 14sp Atkinson Bold at x=18dp, no
  fade — a long name still runs under the track.
- Geometry: row 54dp, pitch 66dp (54 + 12 gap); sled 168×34dp r12; cap
  54×28dp r10, pad 3dp, slot 54dp; recess = two 1.4dp lines @ 0x2E black /
  0x2E white; shadow +2dp @ 0x38; lit edge 1.1dp @ 0x59FFFFFF. No marker line,
  no floor notches, no travel channel. Throw: 260ms CurveA
  cubic-bezier(0.2,0,0,1), same for adjacent and two-stop, no detent event.

## LADDER — each rung independently shippable

### Rung 1 — Truth repairs (no geometry, no new tokens, one branch + two guards)
- **Night material staircase restored inside the cap ceiling (#4A423C max):**
  night PEEK fill `tokControlFill()` → `tokSurfaceBright()` #362C27 (staircase
  step 1, 1.35:1 over ground, below SHUT's cap). OPEN night — currently also
  `tokSurfaceBright()`, which would collide — moves in the same commit to
  the recess itself: fill `tokSurfaceVariant()` #100E0C + label
  `tokTextPrimary()` #D2C6BB. Three night fills, strictly descending:
  #4A423C → #362C27 → #100E0C. Day is untouched (its staircase #252323 →
  #615B5A → #FAF6F0 already descends).
- **First-bind snap:** first tier assignment on a fresh view sets
  `rockerIndex` directly (guard flag, reset never); later assignments animate.
- **No-op silence:** tap resolving to `currentTier` fires no haptic, no
  sound, no callback.
- Motion: 260ms CurveA, unchanged. Haptic/sound: unchanged seat
  (`playFirmClick`, above) minus no-ops. Depth: unchanged.

### Rung 2 — The cap learns to travel (continuous colour + seat marks)
- **Continuous lerp:** cap fill = `lerpARGB` between adjacent stop fills by
  fractional `rockerIndex` (replaces Rung 1's stepped `round()`); each label
  colour = lerp(floor tone, on-cap tone) by (1 − |rockerIndex − i|). Colour
  and position are now the same number on every frame, mid-throw included.
- **Seat line + floor notches (one marker, T3-style):** cap gains an
  18×2.5dp seat line, centred, inset 2.5dp from its bottom edge, in the
  seated label's colour; floor gains one 10×2dp notch per stop (top at
  trackBottom − 5dp, 0x26 black day / 0x33 white night), drawn before the
  cap. Seated = line exactly over notch. Seat-line alpha 35% in transit,
  100% within |rockerIndex − stop| < 0.08.
- **Material grammar completed:** OPEN becomes hollow in *day* too — no
  fill, 1dp inner outline #615B5A day / #776A60 night, label ink. State is
  now amount of material (solid → solid-mid → outline), identical logic in
  both modes, grayscale-true.
- Depth: track top gradient band 6dp (black 12%→0 day / 28%→0 night) added
  over the existing 1.4dp recess lines; shadow stays +2dp @ 0x38.
- Motion/haptic/sound: unchanged from Rung 1 (lerp is a draw change only).

### Rung 3 — A sled you can slide (drag, detents, per-tier feel)
- **Touch rebuilt:** DOWN inside the sled grabs the cap (grab offset kept,
  no jump); MOVE 1:1, `rockerIndex` clamped [0,2]; UP snaps to nearest stop,
  **180ms CurveA**. Tap (DOWN+UP, <8dp movement, same third) = throw,
  **260ms CurveA**. Two-stop tap throw is *ratcheted*: two chained 180ms
  CurveA segments + 24ms dwell at PEEK (384ms total) with the pass tick
  fired at the dwell. **Name-area cycle tap deleted** — name is a label,
  sled is the only gesture object on the row.
- **New engine methods** (existing `triggerWaveform` + `enqueueTransient`
  machinery; `fireFeel` cannot express pitch per its own contract):
  `playSledSeat(tier)`, `playSledPass()`, `playSledGrab()`.
  - Grab: waveform [0,4]ms @ [0,90]; silent (a sound per touch-down in a
    12-row list is noise).
  - Pass (crossing centre 1.0 ±0.02, direction-aware, never on landing):
    [0,4] @ [0,140]; synth 880Hz / 5ms / gain 0.22.
  - Seat SHUT: [0,10,18,7] @ [0,230,0,150]; 440Hz / 14ms / 0.50, chord 0.9.
  - Seat PEEK: [0,7,14,5] @ [0,170,0,110]; 620Hz / 11ms / 0.42, chord 1.1.
  - Seat OPEN: [0,5,12,4] @ [0,130,0,85]; 800Hz / 9ms / 0.34, chord 1.2.
  - Fallback (no engine): one-shots SHUT 8ms@200 · PEEK 6ms@160 · OPEN
    4ms@130 · pass 1ms@110 · grab 1ms@90.
- Depth/tokens: as Rung 2. Dragging state: seat line hidden, all labels at
  floor tone, travel fully visible ahead of and behind the cap.

### Rung 4 — Row geometry converges with the list (52dp + real icons)
- Row 54 → **52dp** (Synthesis item 26), pitch 64dp (52 + 12 gap). Sled
  168×**36dp** r10, cap 54×**30dp** r8, pad 3dp — squarer, more machined;
  sled vertical margin in-row becomes 8dp top/bottom.
- **Pip killed by replacement, not by my wave-1 argument alone:** real
  **28dp app icon** at x=0 (Synthesis 26 / Humans consumer override) takes
  the pip's scan-column job and does it honestly — identity, not a code.
  Name moves to x=36dp, 14sp Atkinson Bold; name field ends 8dp before the
  sled with a 12dp linear alpha fade on overflow (never a hard cut under
  the track, never an ellipsis).
- Icon is a host-layer seam (see CONFLICTS): the Canvas view draws no
  bitmaps today; icon lands in the row's Compose wrapper or as a drawable
  handed to the view — Fenja/Wort's list module decides the plumbing, my
  geometry reserves the 28dp + 8dp gap either way.
- Motion/haptic/sound: as Rung 3. Refused throw (session ratchet) gets its
  body here: 4dp nudge toward target + spring back, **180ms
  MechanicalOvershoot(1.25)**; knock [0,6] @ [0,120]; 180Hz / 16ms / 0.40;
  no state change, no callback.

### Rung 5 — Flick-to-end + sealed state (the speed rung)
- **Flick:** VelocityTracker on the sled; release velocity ≥800dp/s toward
  an end stop throws straight to that end (260ms CurveA), firing the pass
  tick if the middle is crossed. One gesture, worst-case change. Tap and
  drag remain; flick is never the only way to anything.
- **Transit lift:** during any throw, lift = sin(π × segment progress) ×
  1.5dp applied as cap y − lift, shadow y + lift, shadow alpha 0x2E→0x4D at
  peak — the slug visibly rises in its slot and seats back down.
- **Sealed state (session active):** rows are a pre-arm surface; mid-session
  they render exactly as seated (truth: tiers unchanged) but the sled
  swallows touch — no throw, no nudge — and answers a tap with one dull
  knock only: [0,6] @ [0,90], 160Hz / 12ms / gain 0.30. No mid-session
  decisions exist on this surface to refuse one-by-one; the ratchet banner
  stays the screen's one statement of why.

## ★ — priced

**★ Rung 1's night-PEEK branch: night PEEK fill `tokControlFill()` →
`tokSurfaceBright()` #362C27, with OPEN night → recess #100E0C in the same
commit.** Cost: two token swaps in one `when`, zero new tokens, zero layout
/ touch / motion risk, one night render to verify. Buys back the entire
material channel at night — three descending fills under the #4A423C cap
ceiling — which Round 0's peach eviction accidentally flattened. Every
later rung's lerp needs three distinct night stops to interpolate between;
this is the precondition, at branch price. (Wave-1's ★ already shipped;
this is its successor defect, created by that fix.)

## PEAK (≤8 lines)

52dp rows; 28dp real icon, name at 36dp with fade; sled 168×36 milled into
the row, cap 54×30 whose *material amount is the state* — solid #4A423C
night / ink day (SHUT) → mid solid (PEEK) → hollow outline (OPEN).
Colour is a continuous lerp of position; seat line lands on floor notch;
drag 1:1, tap 260ms CurveA, two-stop ratcheted 180+24+180, flick-to-end.
Per-tier seats (440/620/800Hz), blind pass tick at PEEK, silence on no-op.
Mid-session: sealed, knock only. Seven apps SHUT = seven taps, ≈5s.

## STANDARDS CHECK

- **ARM ≤10s:** counted. Tap path = 1 throw per app; setting 7 apps SHUT ≈
  7 taps × ~0.6s aim+tap + one scroll ≈ 4–5s. Drag is the precision path
  (~1.2s/app ≈ 8.4s — inside, never required). Flick keeps two-stop changes
  at one gesture. Deleting the name-cycle removes the wrap trap (a wrap cost
  2 extra taps *and* left a wrong state behind). No rung adds a step, a
  confirmation, or a mode to the arm path.
- **HOLD absolute:** sleds are manual, pre-arm only. No sled implies a
  routine set it — bulk loadout changes render identically to manual ones
  (tier is tier; provenance lives at the loadout band, not on my rows).
  Mid-session the surface seals (Rung 5): nothing on a row can weaken,
  peek, or re-decide anything behind the seal.
- **TRUTH once:** state readable from position + material amount, mid-throw,
  in grayscale — Rung 2 makes colour a function of position so no frame
  disagrees; the pip's second, binary code is gone (Rung 4), replaced by an
  icon that carries identity, not state. Counts live once, in the header —
  not re-rendered per row.
- **His picks:** Atkinson Hyperlegible (names/labels) + Anonymous Pro (the
  "2") only; Sora never enters a row. Warm palette throughout, zero green,
  zero orange on rows in either mode; night staircase tokens as shipped
  (ground #171412, recess #100E0C, cap ceiling #4A423C, text #D2C6BB /
  #9C8E84 / #776A60, cream #FFE4CF text-only on caps). Fonts are real Bold
  files now — no synthesized smear in any rung.

## CONFLICTS & SEAMS

- **Ledger / deflection count → Shield:** NOT annexed. If a deflection
  count lands on Shield, it belongs in the header zone (Wort/Band's
  examination), never on my rows: a per-row count is a second rendering of
  a session fact, competes with name + sled for the row's 52dp, and would
  imply row-level blame the tier doesn't carry. Noted, not designed.
- **Yuki (MaxImpl, sleds):** mailed — my peak changes her spec surface:
  night material staircase (#4A423C → #362C27 → hollow-on-#100E0C), row
  52dp / sled 36dp, flick-to-end, sealed-state knock. Drag, ratchet,
  per-tier seat ladder carry over from wave-1 unchanged.
- **Fenja (search) + Wort (filter words) — list module:** mailed both.
  Rung 4 moves row geometry (54→52dp, pitch 66→64dp, name axis 18→36dp for
  the 28dp icon). Group-by-state and sticky search (Synthesis 26) are
  theirs; my rows make no adjacency assumption, so grouping headers can
  interleave freely. Icon plumbing (Compose wrapper vs drawable-in-view)
  is their call; my geometry reserves the slot either way.
- **Opal (Wild) shutter/proof split, via the wall:** she proposes a shutter
  plate over the track as session enforcement, distinct from the rested
  decision cap. Compatible with Rung 5's sealed state *if* the shutter is
  her part sliding over my track — my cap never pretends to be proof. If
  both ship, sealed rendering is hers to draw, knock is mine. Flagged for
  Ilya/Voss, not resolved here.
- **Hold banked:** not mine, untouched — no sled surface touches the valve.
- **Guard:** still his open pick; nothing here depends on it. If Guard
  dissolves into Shield's list module, my rows gain neighbours, not new
  states.
- Mail sent: Yuki, Fenja, Wort (see mail/); wall post appended to COMMONS.md.
