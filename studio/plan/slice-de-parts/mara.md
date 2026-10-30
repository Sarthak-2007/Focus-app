# SLICE E — MARA (Wild, seat 10, light): D6 hub specular + THE ORANGE EXIT

*Re-seated by Marlowe. My law, unchanged: one rig, fixed in space, light
carries no data; specular is a pure function of where a moving part
currently is. Stoff's §1.4 specular-dimming exception as GRANTED by Ren
is the only shading law used below: a locked token may be shaded by a
stated multiplier (×1.00 / ×0.75 / ×0.50) as a discrete step; dynamic
light sampling — brightness computed from state, velocity, or history —
would cross the law and stays out, in both joints, by name.*

*Code audited: `Focus-app/app/src/main/java/com/example/ui/views/BlockitCoreViews.kt`
(1,419 lines), `.../ui/screens/TactileDeckScreen.kt`,
`.../ui/components/MonolithCustomComponents.kt`, `.../ui/theme/{Color,Theme}.kt`,
`.../ui/theme/MonolithCurves.kt`. All citations are to the shipped Slice C
tree, not to plan files.*

---

## 0. A CORRECTION TO MY OWN BRIEF, BEFORE THE VERDICTS

The brief told me the wheel's swept specular (`specularSwept` + LUTs) is
already built and to reuse it. **In the shipped code it is not.** There
is no `specularSwept`, no LUT, and no prefix sum anywhere under
`Focus-app/app/src/` (grep-verified). What Slice C actually shipped on
the rim is the point-sampled rest form, inline in the knurl loop —
`t = max(0, cos(toothDeg − 220°))`, `spec = 0.16 + 0.66·t³ × toothFlare[tooth]`
(BlockitCoreViews.kt:574-575) — over teeth drawn at fixed screen angles,
with the code's own comment conceding the swept integral "lands the day
the rim itself rotates." The Broken Flare table *is* shipped
(:462-478, compile-time, teeth 17/83 at 0.62/0.68, no writer — my clause
holds in code).

Consequence for this file: neither joint below depends on the swept
form, so nothing here is blocked by its absence. D6 reuses what
genuinely exists — Uhr's §1.4 angular law (DESIGNERS-PLAN §1.4;
uhr.md §8) and the shipped paints/token resolvers. But no builder
should be sent hunting for a LUT that isn't there, and the REGISTER's
"specular: swept form" line describes a plan state, not the tree.

---

## JOINT (a) — D6: THE HUB SPECULAR CORE

### 1. Verdict: **AMEND — D6 stands in law, fails as written against the shipped press. It has no input today.**

D6 (BEYOND-deck.md, Sana/Uhr) is lawful under my rig: the roll is a
function of cap *position* (press travel) only — never velocity, never
count, never state — and the merge already ruled it so (W2: "D6 ruled
under Mara's law"). I confirm that ruling from the light seat. But
specified against the shipped tetrad hub, D6 describes a press that
does not exist:

- **The core is not drawn today — at all.** The hub draw
  (BlockitCoreViews.kt:686-750) is: contact shadow (circle at cy+2dp,
  `0x26000000`, :688-689), flat cap fill `tokSurfaceBright()` (:690-691,
  day `#FAF6F0` / night `#362C27` via :130), 1dp hairline (:692-694),
  guide ring, closure bars, wells. No specular overlay, no gradient, no
  core, on the cap or anywhere on the hub. §1.4 already names the hub
  as rig territory and §3.2 never spent it; Slice C shipped that
  omission faithfully. D6 is a from-zero draw, not a modification.
- **The press travel it reads does not exist.** The shipped press state
  is two fields — `hubPressed: Boolean` and `hubTarget: Int?`
  (:398-399). ACTION_DOWN inside the hub sets them and returns
  (:767-775) with **no `invalidate()` and no visual change of any
  kind**: no depression, no tonal step, no shadow change. ACTION_MOVE
  does nothing for a hub press (only the wheel-drag branch acts).
  ACTION_UP commits iff the release lands within `innerHubR × 1.1`
  (:818-838): feel, callback, count changes via the
  `studyBlocksCount` setter (:360-382) — the first frame on which the
  press has any visible consequence is the commit frame itself.

So "under press," in the shipped app, the light can see exactly one
bit, and it arrives at the same instant as the commit. A core keyed to
that bit would not roll — it would *jump*, on the commit frame, in
lockstep with the count change. That is the one form my law forbids:
light whose only motion is simultaneous with a count change *is* a
second rendering of the count change, however it is parameterised.
**D6 as literally written (roll "over the press travel") is
unbuildable until the press has a travel.**

### 2. What the filed law already says — the amendment is a restoration, not an invention

The press D6 presumes is not D6's invention. It is filed twice over:

- DESIGNERS-PLAN §3.2 (the hub): "DOWN = +1 tonal step + 1dp depress;
  count commits on **release**."
- Kin M-04 (designers-parts/kin.md): hub dip on PRESS
  `cubic-bezier(0.20, 0.00, 0.30, 1.00)`, **45ms** to the bottom of the
  dip; contact shadow collapses.

Slice C built the release half of that law (commit-on-release,
slide-off cancels — Mikro's correction) and silently dropped the DOWN
half. The task's phrase "DOWN moves nothing" is true of the *count*
and false of the *filed press*. My amendment restores the filed DOWN
half — depression only, count timing untouched — and D6 then has the
input it was always written against. Nothing in Slice C's commit
semantics changes: target still captured at DOWN (:776-797), commit
still iff UP inside ×1.1 (:818), cancel still silent.

### 3. Amended spec — exact

**A1. The rest core (new draw, hub face).** In `RotaryWheelView.onDraw`,
immediately after the cap fill (:691) and before the hairline
(:692) — i.e. light on the cap's surface, *under* the guide ring,
closure bars and wells (rig never washes the count's own geometry) —
draw the §1.4 core, clipped to the cap disc (radius `innerHubR`,
:490, = outerR × 0.494):

- Law (Uhr §8, verbatim parameters): for a point on the cap at azimuth
  φ measured around the hub centre, Δ = angular distance of φ from
  **315°**; `k(Δ) = 1` for |Δ| ≤ **9°**; `k(Δ) = cos²((π/2)·(|Δ|−9°)/25°)`
  for 9° < |Δ| < **34°**; `k = 0` beyond. Peak lift: **10% day / 8%
  night**, tint day `#FFFFFF` / night `#EDE3D8`.
- Implementation form: one `SweepGradient` centred on the hub centre,
  colour stops sampled from k at ≤3° intervals (plateau and cos²
  sampled, never hand-tuned), built **once in `onSizeChanged`** and
  reused — no per-frame shader construction (Dario's rig discipline).
  Night check by construction: 8% of `#EDE3D8` mixed over the night
  raised cap `#362C27` lands ≈ `#443B35`, under the `#4A423C` night
  cap ceiling — Mara's night clamp holds without a clamp pass.
- The core is drawn in **every** hub state: all counts 1–8 and FLOW
  (in FLOW the press registers nothing, :772-773, so the core simply
  never leaves rest — the cap is still a cap in the same room).
  It reads no field except hub centre, mode, and press depth (A2).
  **Count-invariance is structural: `studyBlocksCount` is not an
  input.** Frame Equality holds at rest at any hour.

**A2. The press depth (restored filed input).** One new field,
`hubPressDepth: Float ∈ [0,1]` — the cap's depression as a fraction of
its filed 1dp:

- DOWN inside the hub (the :767 branch): start a 45ms animator,
  PRESS curve `cubic-bezier(0.20, 0.00, 0.30, 1.00)`, from current
  depth → 1. (Neither PRESS nor SEAT exists in the shipped
  `MonolithCurves.kt`, which carries only CurveA/B/C — both
  interpolators are added there, plan values verbatim.)
- MOVE while `hubPressed`: if the pointer crosses outside
  `innerHubR × 1.1` (the same radius the commit test uses, :818),
  the cap is being abandoned — start the return (A3) from current
  depth. Crossing back inside re-dips (PRESS, 45ms, from current
  depth). The shipped commit rule is untouched: UP outside ×1.1
  still commits nothing, whenever the crossing happened.
- Draw effect of depth, all inside the existing hub group:
  (i) hub contents (cap, core, ring, bars, wells) translate
  **+depth × 1dp** in y — toward the panel; the contact shadow stays
  at its rest offset (:689), so the shadow gap closes 2dp → 1dp as
  the cap seats down (Kin's "shadow collapses," at Canvas scale);
  (ii) **the core translates depth × 2dp toward 135°** — canvas
  direction (+0.707, +0.707) × 2dp — applied as the SweepGradient's
  local-matrix translation. Core offset is a function of depression
  in dp and of nothing else: same depression ⇒ same offset, at any
  count, any hold duration, any approach speed. The animator moves
  the *cap*; the light reads the cap.
- No fill change. §3.2's "+1 tonal step" is **not** built: on the day
  staircase the hub already sits at the top step (`#FAF6F0` raised —
  Stoff's day staircase has no step above it), so a day tonal step
  does not exist to take. Depth + shadow + light carry the press.
  (Noted here so the omission is a ruling, not an oversight.)

**A3. The return (commit frame law).** On ACTION_UP (commit or
drag-off) and on ACTION_CANCEL, in the :815 branch, start the return
animator **on that same frame**: depth → 0 under **SEAT
`cubic-bezier(0.22, 0.90, 0.28, 1.00)`, 160ms** — the hub's own
shipped settle duration (closure ink-settle, :371). The core rides
depth back to its identical rest offset; arrival per the arrival law
(first frame within max(1% of travel, 0.5dp)). On a commit, the
return's first frame is the count-change frame (the
`studyBlocksCount` setter's invalidate, :380) — D6's "returns … on
the same frame the count commits" is satisfied as *start-frame
identity*, with the travel itself taking the SEAT duration. A return
that snapped in 0ms would be a second jump keyed to the commit —
the exact failure A1–A2 exist to prevent.

**A4. What never moves the light.** Count (1–8, closures, wraps),
FLOW entry/exit, wheel angle, velocity of anything, hold duration
past the 45ms dip (a held cap sits at depth 1, core at full 2dp
offset, indefinitely, still), session state. If any of these ever
reaches the core's inputs, it is a writer under my law and reverts
on sight.

**A5. Perceptibility gate (D3's method, applied to my own joint).**
2dp at 10%/8% peak is near the floor of visibility. Device pass: if
the roll cannot be seen at arm's length on the real hub, the *travel*
dies and A1's rest core ships alone — a roll nobody can see is not
worth a press-depth machine. The rest core is not gated: it is §1.4
territory finally spent.

### 4. Code sites (a)

- `BlockitCoreViews.kt:686-694` — core draw inserted between cap fill
  and hairline; hub-group translate wraps :686-750.
- `BlockitCoreViews.kt:398-399` — `hubPressDepth` + animators beside
  the existing press fields.
- `BlockitCoreViews.kt:767-838` — DOWN starts dip; MOVE gains the
  ×1.1 crossing test; UP/CANCEL starts the SEAT return.
- `BlockitCoreViews.kt` new `onSizeChanged` in `RotaryWheelView` —
  SweepGradient construction (hub size is view-derived, :490).
- `MonolithCurves.kt:25-27` — PRESS + SEAT PathInterpolators added
  beside CurveA/B/C.

### 5. Price (a)

**≈80 lines total, no state, no persistence, no new tokens.**
Rest core alone (A1): ≈35 lines. Press machinery (A2+A3: field, two
animators, touch changes, translate): ≈45 lines. Split is stated so
Ren can buy A1 alone if the fork goes that way.

### 6. Risks (a)

- SweepGradient wrap seam: the gradient's 0° start must sit inside
  the 292° zero zone; if a builder starts it inside the 68° lit
  footprint, a hard seam crosses the cap. Construction note in A1
  (sample k around the full circle; the zero zone swallows the seam
  by arithmetic, not by care).
- The dip makes DOWN visible for the first time on this control. If
  any other Slice C/D surface priced "hub press is invisible" into a
  test or a sibling spec, that price is now wrong — flagged for
  Marlowe's merge, not silently absorbed.
- 45ms dip vs the brush floor: a sub-45ms tap commits (shipped law —
  release commits regardless of dip completion) with the core caught
  mid-roll; A3 returns from *current* depth, so the light stays a
  pure function of position even then. No commit gating is added —
  the dip never taxes ARM.

---

## JOINT (b) — THE ORANGE EXIT (Vertigo: Departure, t = 0–200)

### 1. Verdict: **AMEND — Wren's four steps stand; the carrier list in every filed version is incomplete, and two of the real carriers are not in the view everyone will look in.**

The shot's law (Wren, merged W4): from t=0 (REGISTER: the proof-seal
frame) the commit fill takes **4 discrete tone steps at 0/50/100/150ms
→ ink; zero orange from t=200**; tone steps, never gradients; input
dead from t=0. I confirm the mechanism and the clock from the light
seat. What no filed version does is enumerate what is actually orange
on the Deck at that frame. The audit below is grep-complete over the
Deck composition (Deck screen + its two Canvas views + the chrome
that Wren's grammar keeps in frame), and it finds **three carriers
present at the seal frame and three more the seal itself gives birth
to** — which, unpriced, would re-orange the shot from inside its own
first 200ms.

### 2. Carriers AT the seal frame (t = 0⁻, idle Deck as photographed)

| # | Carrier | Site | Value at t=0⁻ |
|---|---|---|---|
| B1 | **Slide cap fill** — the commit object itself | `SlideToStartView.onDraw`, BlockitCoreViews.kt:1133 (`tokAccent()`, :139) | day `#F55314` / night `#F47442`, full strength |
| B2 | **Slide progressive trail** — the wash behind the cap, drawn at `dragFraction ≥ 0.01`; at the seat (fraction 1.0) it spans the whole travelled track | BlockitCoreViews.kt:1104-1106 (`paintAccent = tokAccent()`, alpha **55**) | accent at α55/255 over the block-bar track |
| B3 | **Done sub-step numerals** — `01`, `02`… in the Deck task line, **conditional**: present only if the task's steps are expanded at the seal and ≥1 step is done | TactileDeckScreen.kt:436 (`scheme.primaryContainer`) | day `#F55314` / night `#F47442` text |

That is the whole at-frame list. Audited and **clean**, so nobody
re-opens them: the wheel reader dot (ink/glyph by construction,
BlockitCoreViews.kt:676-680 — its stale "Accent" comment at :666 is a
lie in a comment, not in code), the hub (no accent input), the FLOW
key (ink control fill), the hero (ink/glyph), the slide's label,
badge and grip line (`LIGHT_ON_PRIMARY` cream — :1112, :1120, :1143 —
they sit *on* orange but are not orange, and they remain lawful on
the terminal ink cap unchanged), the nav cap's session centre dot
(`tokTextPrimary()`, :1352-1354 — the precedent B5 below follows).

B3 deserves one sentence of its own: orange numerals for "step done"
are already a ration breach in spirit (Stoff rule 10: orange is one
commit fill per screen, and the slide holds the Deck's ration) — but
they exist in the shipped tree, they are orange at the seal frame,
and the exit owns them while they live. Their permanent fate is
Tasks' seat's, not mine.

### 3. The four steps — exact values, exact times, every carrier

Shading law: Ren's granted §1.4 exception only — each step is the
locked accent token shaded by a stated multiplier, channels multiplied
and rounded half-up. **No interpolated hexes** (H-7): every value
below is either a locked token or a granted shading of one.
Steps land on vsync boundaries at **t = 0, 50, 100, 150ms** from the
shot's t=0, held without interpolation between steps (a crossfade
between steps would be a gradient wearing a schedule). At 60Hz and
120Hz alike, 50ms is a whole number of frames (3 / 6) — the steps are
frame-exact on both panels this app will meet. Step 4 is terminal
from t=150; **zero orange at t ≥ 200** is met with 50ms of margin —
the guarantee is the terminal token, not the clock.

| Step | t | Multiplier | B1 cap + B2 trail (α55, unchanged) + B3 numerals — DAY | …— NIGHT |
|---|---|---|---|---|
| 1 | 0ms | ×1.00 | `#F55314` (locked accent) | `#F47442` (locked accent) |
| 2 | 50ms | ×0.75 | `#B83E0F` | `#B75732` |
| 3 | 100ms | ×0.50 | `#7B2A0A` | `#7A3A21` |
| 4 | 150ms | — terminal token | see below | see below |

**What each carrier becomes at Step 4 (tone, never transparency):**

- **B1 cap → the control fill**: Canvas `tokControlFill()`
  (BlockitCoreViews.kt:133) — day `#252323` / night `#4A423C`. This
  is the merged law's "commit fill is ink," with the night half
  stated correctly: at night the ink *fill* is the cap tone, not
  cream — a cream cap would be the brightest mass in the shot's
  settle. Grip line and lit top edge (:1137, :1143) unchanged; the
  cream grip on the night cap fill is `tokOnControlFill()`'s own
  pairing (:137). The cap does not fade, shrink, or spring home
  during the exit (see Risk R2).
- **B2 trail → the same control fill at the same α55**, over the
  block-bar track (day `#2E2A26` / night `#161311`): day, near-invisible
  by arithmetic; night, a faint warm wash one step above the track.
  It remains a *tone on the track* until the slide leaves the
  composition under Wren's staging — at no frame is it transparent
  over ground, and at no frame after t=150 is any pixel of it orange.
- **B3 numerals → their own undone tone**: `scheme.onSurfaceVariant`
  — day `#5F5856` / night `#776A60` (Color.kt:23/44 via Theme). Done
  stays said by the title's strike-through (TactileDeckScreen.kt:443)
  and the numeral's place in the row; the exit does not invent a
  replacement signal. Steps 1–3 shade the numeral by the same
  multipliers (it is the same token leaving by the same door).

The exit's driver is a single phase value (0–3) computed from the
shot clock; both Canvas (B1/B2 via a shaded-accent resolver beside
`tokAccent()`, :139) and Compose (B3) consume the *same* phase, so
the carriers can never disagree about which step the room is on.
Dynamic sampling is not used: the phase is the shot's own timeline —
a position in a filed animation — not a reading of session state.

### 4. Carriers BORN at the seal — the exit's border

The seal recomposes the Deck. Three orange objects are created by
that recomposition, inside the shot window, in the shipped tree. An
exit that drains B1–B3 while these are born orange is theatre. Each
is therefore re-toned **at birth** — permanently, not as a fifth
step; they never hold the accent, so they never need the steps:

| # | Carrier | Site | Becomes |
|---|---|---|---|
| B4 | **Running progress fill** — born the frame the running branch composes | TactileDeckScreen.kt:346 (`.background(scheme.primaryContainer)`) | `inkControlFill()` — already imported in this file (:50) and already the FLOW key's fill; day `onBackground #2C2A29` / night `#4A423C` (MonolithCustomComponents.kt:439-443) |
| B5 | **Chrome session dot** — the header's brand dot turns accent when the session activates; Wren's grammar keeps chrome in frame for the whole shot | MonolithCustomComponents.kt:227 | `scheme.onBackground` — day `#2C2A29` / night `#D2C6BB`, i.e. exactly the Canvas nav dot's `tokTextPrimary()` precedent (:1352-1354: "the filled centre says 'session live'; orange says nothing here"). Chrome's zero-orange law (Wren) is currently violated by this one dot at every seal |
| B6 | **GRACE WINDOW ABORT chip fill** — the running Deck's first control (elapsed ≤ 10s, so it is on screen during the shot's settle) — and its badge knock-on | TactileDeckScreen.kt:512 (`isAccentArmed = true`); badge via MonolithCustomComponents.kt:180 (`monoColor = primaryContainer` whenever a chip is primary-ink and not accent-armed) | chip called as `isPrimaryInk = true`; :180's badge rule loses its accent branch — badge takes the chip's foreground (cream on ink). This also de-oranges the EMERGENCY BREAK chip's badge, same line, same reasoning: **the running Deck carries zero orange, at every second of the session, not just the first 200ms** |

B4–B6 are priced separately below so Ren can cut them without
touching B1–B3 — but my recommendation is on the record in §7: an
orange exit with an orange birth inside it is not an exit.

### 5. Code sites (b)

- `BlockitCoreViews.kt:139` — shaded-accent resolver beside
  `tokAccent()` (the ×1.00/×0.75/×0.50 shading op, one place, both
  joints' shading needs — it is also the only lawful way any future
  seat shades a locked token).
- `BlockitCoreViews.kt:1104-1106, 1133` — B2/B1 read the shaded
  resolver × exit phase instead of raw `tokAccent()`.
- `TactileDeckScreen.kt:436` — B3 numeral colour: shaded
  `primaryContainer` by phase, terminal `onSurfaceVariant`.
- `TactileDeckScreen.kt:346` — B4. `TactileDeckScreen.kt:512` — B6
  callsite. `MonolithCustomComponents.kt:180` — B6 badge rule.
  `MonolithCustomComponents.kt:227` — B5.
- Phase source: the Slice E shot controller's `vertigoT` (Wren's
  joint). If the exit must ship standalone, a Choreographer callback
  in the Deck screen stepping phase at 50ms boundaries from the seal
  instant is ≈15 lines and dies the day the shot controller lands.

### 6. Price (b)

**≈50 lines total.** Core exit (shading resolver + B1/B2 + phase
plumbing + B3): ≈40 lines piggybacked on the shot clock, ≈55
standalone. Born-carrier border (B4+B5+B6): **4 lines** — three
one-line re-tones and one one-line rule change. The cheapest honesty
in this file.

### 7. Risks (b)

- **R1 — the scheme flip at t=0 (blocking dependency, Wren copied).**
  `MyApplicationTheme` selects the *night* scheme whenever a session
  is active (Theme.kt:104-108), and the Deck's Canvas views take
  `effectiveNight = isDarkTheme || isSessionRunning`
  (TactileDeckScreen.kt:119). In a **day** seal, the shipped tree
  therefore swaps the accent `#F55314 → #F47442` — a *brightening* —
  on the very frame the exit starts, and flips every other token
  with it. The exit as specced shades **the accent as drawn at
  t=0⁻** (pre-seal mode). For that to be true on device, the shot
  must hold the pre-seal scheme through the exit (the merged shot
  already delays its one ground step to t=400 — the scheme question
  and the ground step are the same question). If the builder lets
  the theme flip at t=0, Step 1 is a lie and the day exit runs its
  first step backwards. This needs one ruling in the merge (Fork
  F3) — it crosses my joint and Wren's, and neither of us can settle
  it alone.
- **R2 — the shipped spring-back.** On commit, the shipped slide
  springs the cap home (`animateSpringBack(0f)`,
  BlockitCoreViews.kt:1186) — the exit presumes a cap *seated*
  through t=200 (merged §3.3: "sled never springs home on success").
  If Slice E builds the exit before the seat law, the cap exits
  orange *while travelling home* and B1/B2's steps land on a moving
  target. Sequencing note for Marlowe, not a spec change.
- **R3 — B3's conditionality.** A builder testing with steps
  collapsed will never see B3 and may ship the numeral unshaded; the
  acceptance check must include one seal with steps expanded and a
  step done. One line in the test, or the carrier returns.

---

## 8. FORKS FOR REN

- **F1 — the hub press (Joint a).** (i) **Restore the filed DOWN
  depression** (PRESS 45ms / 1dp / SEAT return) and D6 rolls as
  amended — *my recommendation*: the dip is already filed law
  (§3.2, Kin M-04); Slice C shipped half of it; the count's law
  (nothing commits on DOWN) is preserved exactly. (ii) Ship the rest
  core alone (A1, ≈35 lines) and strike D6's travel — lawful, and
  the hub is lit, but the one press-light in the app dies. (iii)
  Kill D6 whole. I argue against (iii) on §1.4: the territory was
  granted and never spent; an unlit hub beside a lit rim is two
  rooms, not one.
- **F2 — the exit's border (Joint b).** Extend the exit to the
  born-at-seal carriers B4–B6 (4 lines) — *my recommendation*, for
  the reason in §4: without it, orange returns inside the same shot
  that is busy removing it, twice in chrome's own frame. Or scope
  the exit to B1–B3 and let the running Deck keep its orange
  furniture — in which case W4's "zero orange from t=200" should be
  amended in print to say what it would then mean: zero orange
  *from the slide*.
- **F3 — the scheme freeze (dependency, raised for ruling).** Hold
  the pre-seal colour scheme from t=0 until the shot's ground step
  (t=400), so the exit shades the accent the user actually saw
  commit — *my recommendation*. The alternative (flip at t=0 as
  shipped) makes every day seal open with a brightening step and
  should be rejected on TRUTH, not taste: the exit would render the
  accent as a colour it never was on the Deck he armed.

## 9. MAIL / WALL NOTE

Inbox at start: one unread letter — Cato (0710, thread
`the-tailors-hand`) — read and moved to `read/`. My answer, sent to
Cato's inbox and posted to the wall with this filing: **his reading
of my clause is correct, and I state its scope precisely as he
asked.** The Broken Flare's no-writer clause governs *renderings
that imply a past* — any runtime write that makes light (or any
material cue) claim a history. EASE's fittings are none of that:
they are state changes to feel parameters, printed in full, applied
only on his tap, constant between fittings — CUT 1 → CUT 2 as
lawfully as a loadout change, and my Frame Equality Test passes
inside every plateau because the *state itself* changed, in the
open, by his hand. The clause's teeth stay where they belong: a
parameter that drifts silently, or a rendering that wears a past it
didn't declare, reverts on sight. If Marlowe rules feel is state at
every timescale, that ruling — not my clause — is what EASE falls
to, and Cato has already conceded he won't appeal it. One signed
wall entry appended on filing (08:1X IST): both verdicts, the
missing-LUT correction, and the clause scope, where every seat can
attack them.

— Mara, Wild · light
