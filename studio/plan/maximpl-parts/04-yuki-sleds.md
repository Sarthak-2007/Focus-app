# 04 — SLEDS: THE APP-ROW 3-STOP THROW + THE SHIELD APPS LIST (PLAN WAVE)

**Seat:** Yuki — Maximum-Implementable (coordinator: Voss)
**Element:** `AppRowSledToggleView` (Shield's per-app `SHUT / PEEK 2M / OPEN` sled) and the apps list that hosts it in `ShieldMatrixScreen.kt`.
**Status of this document:** TARGET spec. Where current code already matches, it says KEEP; every deviation is named as a delta in *Code: helps / fights*. No code in this wave.
**The one-sentence version:** the sled becomes a thing the finger *throws* — drag it, fling it, it lands once, with one sound, owned by the view — and the words leave the caps forever: position is the state, one word at the row's margin says it back, and it only changes its mind on arrival.

**Geometry:** track 168×36dp, radius 12dp, pad 3dp, slot 54dp, cap 54×30dp r8 (track height 34→36dp and cap 28→30dp r8 adopted from Schlitten/Marko convergence — the r8 cap also matches the app-wide radius freeze, 8dp keys; slot width and travel are untouched). Row height changes 54→52dp (see *The list*). Track milled-slot drawing from wave-1 §2 is adopted whole: hairline outline (`tokHairline()` 1dp, the sibling `SledSwitchView` idiom), end walls (dark 0x2E000000 left / light 0x2EFFFFFF right, 1.4dp), three printed detent ticks on the track floor (hidden tick = the stop you're on = remaining travel always shown), slot dividers at 40% alpha, travelled-floor rect (0x12000000 day / 0x18FFFFFF night, left pad → cap centre). No gradients, no new tokens, no new dependencies.

---

## Mechanism

### Anatomy of one row (52dp tall, full-bleed, hairline under)

Left → right, all x positions from the row's own left edge (host margin is the frozen 20dp):

| Part | Geometry | Rendering |
|---|---|---|
| Status lamp | glyph box 8dp, centre x=10dp, vertically centred | 3 silhouettes, §State grammar below |
| App icon | 28×28dp at x=18dp, vertically centred | the app's real OS icon, unmasked |
| App name | starts x=54dp, 14sp Atkinson Hyperlegible (real Bold file), `tokTextPrimary()`, single line, ellipsised | inert — text, not a target |
| State readout word | right-aligned, its right edge 8dp left of the track; fixed 44dp zone so every track on the screen aligns in one column | 10sp Anonymous Pro caps, `tokTextSecondary()`, real Bold file |
| Sled track | 168×36dp, right edge flush with row right edge, vertically centred (8dp margins in the 52dp row) | milled slot, above |

### The throw contract (this exact contract is mailed to Sten for the routine mini-sled)

The sled owns one continuous value, `animatedPosition ∈ [0f, 2f]` (0=SHUT, 1=PEEK, 2=OPEN). Everything visual is a pure function of it. The contract:

1. **DOWN answers.** Finger down inside the touch rect (track bounds extended 12dp left, full row height vertically) → the view claims the gesture: `requestDisallowInterceptTouchEvent(true)`, animator cancelled *at its current position* (a mid-flight cap can be grabbed — position is never teleported), press state renders **the same frame**: contact shadow contracts +2dp→+1dp and fades 0x38→0x2A000000, lit top edge dims 0x59→0x33FFFFFF. No sound, no haptic on DOWN — a press claims nothing about state, so it says nothing.
2. **Grab offset, never absolute.** `grabOffset = touchX − capLeftPx` recorded on DOWN. On MOVE, `animatedPosition = clamp((touchX − grabOffset − trackLeftPad) / slotW)`. The cap stays under the finger exactly where the finger took it. (The current code has no MOVE at all; the wheel's absolute-atan2 teleport is the anti-pattern, named in Rad's plan too.)
3. **Drag is 1:1, ends resist.** Inside [0,2]: 1:1. Past either end: the cap follows at 0.32× for at most 6dp of visual overtravel, then hard-stops. No detent magnetism while the finger is down — the finger is the authority; magnetism is for the release.
4. **Scroll surrender.** Until horizontal movement passes touch slop (8dp), the gesture is undecided. If vertical movement exceeds 12dp *and* 1.5× the horizontal movement first, the view surrenders: treated as CANCEL (below), intercept released, the list scrolls. A tired thumb scrolling the list must never drag a sled by accident — and a sled drag must never scroll the list. One of them wins inside 12dp, every time.
5. **Release decides by position, then velocity.** On UP: base target = nearest stop. If release velocity (last two MOVE samples) ≥ 800dp/s in a direction, target = nearest stop + 1 in the fling direction (clamped) — a flick crosses one extra stop, never two; the hand stays the author. The view then asks the ViewModel to commit (see *State machine* — acceptance is synchronous) and, if accepted, settles.
6. **Tap is a throw, not a shortcut.** UP within slop and <300ms = tap: target = the stop whose slot contains the lift point. Same settle, same landing feel, same commit path as a drag. A tap on the stop the cap already sits on is a **null tap**: press visual, release, nothing fires — no callback, no DB write, no click. A control that clicks for doing nothing is lying about having acted.
7. **CANCEL returns in silence.** ACTION_CANCEL, scroll surrender, or detach mid-gesture: settle back to the committed stop, 180ms CurveA, **no landing feel, no callback**. Nothing landed; nothing is announced.
8. **Exactly one commit per gesture.** `onTierSelected` fires once, at settle start, only after acceptance. The landing feel fires once, at settle end, only from the view (see *Sound*). An interrupted settle (new DOWN) disarms the pending feel — the throw that never landed never sounds.

### Tap-on-name: KILLED

Wave-1 found it: tapping the app name (or anywhere left of the track) silently cycles LOCKED→PEEK→OPEN→LOCKED. Its fate is death, for three reasons that are each sufficient:

- **TRUTH:** it is an invisible control. Nothing on the name says it acts. A control you cannot see is a state change you cannot attribute — the exact failure TELL THE TRUTH exists to kill.
- **The wrap is a landmine:** OPEN→SHUT in one tap on a *name*, and worse, SHUT→PEEK — one stray touch while reaching to scroll *weakens a shut app*, the most dangerous direction, with the least deliberate gesture in the app.
- **One fact, one control:** the sled is the tier control. A second, hidden, worse control rendering the same fact violates the workshop law and guarantees the two drift in feel (the cycle has no throw at all).

Replacement: name and icon are inert. The row has exactly one touch target — the sled, with the generous touch rect above (full row height, +12dp left) so the tired hand loses nothing it actually used. `MonolithViewModel.toggleAppBlocked()` / `cycleAppBlockTier()` lose their only UI caller and are marked dead in *Code: fights*.

---

## Motion (curve + ms)

All curves and durations are the existing `MonolithCurves` vocabulary. Settle interpolator is `MechanicalOvershoot(1.25f)` for every landing throw: the cap passes its stop by ~2–3dp and seats back — the overshoot *is* the landing, drawn. Returns (cancel/reject) use `CurveA` — a return must never bounce past home.

| Beat | Curve | Duration | Notes |
|---|---|---|---|
| Tap throw, 1 stop | MechanicalOvershoot 1.25 | **260ms** | same clock as today's throw; velocity now honest because distance is fixed |
| Tap throw / fling, 2 stops | MechanicalOvershoot 1.25 | **420ms** | duration scales with travel — wave-1's fixed 260ms made 2-stop throws move at double speed |
| Drag-release settle, <0.5 stop to travel | MechanicalOvershoot 1.25 | **180ms** | distance table, measured on `|target − animatedPosition|` at release |
| Drag-release settle, 0.5–1.5 stops | MechanicalOvershoot 1.25 | **260ms** | |
| Drag-release settle, >1.5 stops | MechanicalOvershoot 1.25 | **420ms** | |
| Return (CANCEL / ratchet rejection / surrender) | CurveA (0.2,0,0,1) | **180ms** | no overshoot, no feel |
| Programmatic settle (loadout apply, external state) | MechanicalOvershoot 1.25 | **260 / 420ms** by distance | silent — see *Sound* |
| Lamp settle pulse | CurveA | **180ms** | glyph radius ×1.3 → ×1.0 from the landing frame |
| Press sink / release restore | — | **0ms** | a state, not an animation; renders on the DOWN frame itself |
| Row placement on filter/search/group changes | CurveA | **260ms** | `animateItem()` placement (Fenja Rung 3); headers appear/vanish instantly, sleds themselves never animate height |

Animator plumbing: one `ValueAnimator` per view, reused; `onAnimationEnd` is the single landing event (feel + readout flip + pulse start all hang off it). `onAnimationCancel` disarms. If the platform animator duration scale is 0 (reduced motion), the settle snaps — the landing feel still fires exactly once; feel is not motion.

---

## Haptics (per beat)

Every feel on this element is exactly one `fireFeel(...)` call from the view. With an engine attached, that call *is* the complete feel event — `TactileSoundEngine` renders the sound and its paired waveform; the Round-0 contract stands: no second vibrator one-shot anywhere on this path (the old double-fire mush does not come back through a side door). With no engine (previews), `fireFeel`'s existing fallback vibrates the selector value as a one-shot duration — still one call, still one owner.

| Beat | fireFeel selector | Paired primitive | Why this weight |
|---|---|---|---|
| DOWN (press) | — none — | — | press is a claim of nothing |
| Drag crossing a half-stop boundary (0.5 / 1.5), finger down only | `1L` | `playRotaryDetent(560Hz)` | a position tick, not a state claim — the finger crossed a printed line. Hysteresis 0.08 stops so boundary jitter can't re-fire; max one tick per crossing |
| **Landing → SHUT** | `4L` | `playMechanicalLatch(engaged=true)` | the heaviest body in the engine for the heaviest state — a gate seating |
| **Landing → PEEK 2M** | `8L` | `playFirmClick()` | bright, short, mechanical — a latch, not a seal |
| **Landing → OPEN** | `1L` | `playRotaryDetent(820Hz)` | the soft detent, highest and quietest — opening is a release, not an event |
| Return / rejection / surrender | — none — | — | silence is the truthful sound for "nothing happened" |
| Null tap (same stop) | — none — | — | |

There is no DOWN haptic, no UP haptic, and no mid-settle haptic. One gesture produces at most: drag ticks (position truth) + one landing (state truth).

---

## Sound (primitive + pitch)

**Single owner, named: `AppRowSledToggleView`, at `onAnimationEnd` of its own settle animator.** That is the whole sound design's load-bearing sentence. Consequences, all deltas:

1. **The ViewModel goes silent for app tiers.** `MonolithViewModel.setAppBlockTier()` currently fires `soundEngine.playRotaryDetent(freq)` (320/560/820Hz by tier) *in addition to* the view's `fireFeel(8L)` — two transients and two waveforms stacked per change, fired before persistence, and fired even when the one-way ratchet then rejects the change. **Delete the VM's detent call.** The VM's tier pitch map (320/560/820) is not lost: 560 and 820 move into the view's landing/tick table above, where pitch is rendered by the only pitched path (`1L → playRotaryDetent`). 320Hz dies — SHUT's voice is the latch's body, not a pitch.
2. **Programmatic changes are silent.** When a tier changes without this view's gesture (loadout apply animating twelve rows, repository sync), the cap settles with the same motion but `feelArmed = false`: twelve landing sounds for one loadout tap would be a lie about how many acts occurred — one act (the loadout tap, which has its own feel on the band) happened.
3. **Acceptance precedes sound.** Because the view commits through the synchronous acceptance check (*State machine*), a rejected throw produces no sound at all — today it produces a success click for a change that never happens. The ratchet banner (existing, host-level) carries the rejection in words; the sled carries it in a silent return.
4. **No other call sites.** No sound on DOWN, MOVE (beyond the drag tick, which is the `1L` detent at 560Hz — position, not state), UP, CANCEL, scroll, group toggle, search, or filter. `playHarmonicSeal` and `playCompletionPulse` are not used on this screen's rows — sealing and completion belong to the Deck.

---

## Depth & light

One light source, above-left, obeyed by every line in the row — and per the studio's light law, the light *means nothing*: no highlight encodes state, progress, or armed-ness. Specular is a pure function of where the cap is and whether a finger holds it.

- **Track:** recess shading as today (top inner line 0x2E000000 1.4dp, bottom inner 0x2EFFFFFF 1.4dp) + wave-1's end walls, outline, detent ticks, travelled floor. The track is the deepest thing in the row; the row itself has no background, no card, no elevation — ground shows through.
- **Cap light — the Travelling Specular, consumed not forked** (Dario owns the rig; sleds take its linear form): an 18dp specular band inside the cap's clip, gradient precomputed once in `onSizeChanged` (stops 0/.35/.5/.65/1, peak day 14%, night 8% of #FFF6EC), centre offset **bounded and seat-relative** per Dario's correction: `bandOffset = clamp(−0.35 × localTravelPx, −8dp, +8dp)` where `localTravelPx` is distance from the *nearest* seat — offset is zero at every seat (a rest offset would encode position, which light must never do), the band rakes on departure and settles centred on arrival, keyed to `animatedPosition` — the light moves because the cap moved, never because the cap *means* anything. On night #4A423C fills the body peak drops to 4% and the 1px catch edge (12%) carries the effect. The band always draws under the marker. This supersedes the old static lit-top-edge line; the catch edge is that line, now position-driven.
- **Cap at rest:** contact shadow 0x38000000 at +2dp.
- **Cap held (PRESSED/DRAGGING):** shadow +1dp at 0x2A000000, specular peak halved — the cap sits *down* into the hand. Restores on release frame, before the settle starts. (Press dimming is touch truth, not light-as-data: it says "held", which is a fact about the finger, not the tier.)
- **Cap in flight:** rest shadow; the overshoot reads against the end wall shadow; no motion blur, no trail, no glow.
- **Night:** ground #171412; cap fills per the staircase in *State machine* (SHUT #4A423C cap → PEEK #362C27 → OPEN hollow). Readout word at night is `tokTextSecondary()` = #9C8E84 — the 5.8:1 text tier. Lamp glyphs `tokTextPrimary()` (11:1 tier) for SHUT/PEEK, `tokTextSecondary()` for OPEN's dash. Nothing in the row binds the 3.5:1 decorative tier to anything tappable or textual. **Zero orange anywhere in the row** (Shield accent budget = ZERO — no `tokAccent()`, no `tokPrimaryContainer()`), **never green**.

---

## State machine

Three variables, never conflated:

- `committedTier` — the tier in state (ViewModel/DB). The truth.
- `animatedPosition` — where the cap is drawn. A pure rendering value.
- `readoutTier` — what the margin word and the lamp claim. **Invariant: `readoutTier == committedTier` at every rest frame.** It changes only in the landing event, in the same frame the commit is confirmed — flip on arrival, never mid-flight.

Gesture states: `IDLE → PRESSED → DRAGGING → SETTLING → IDLE`, with `RETURNING` as the silent settle variant, and CANCEL reachable from PRESSED/DRAGGING.

**Commit protocol (the acceptance gate):**
1. Gesture ends with target ≠ committed → view calls `onTierSelected(target)`; the chain (screen → `MonolithViewModel.setAppBlockTier`) must **return Boolean accepted**. Today it returns Unit and the ratchet rejects silently downstream — delta: the VM returns `false` on ratchet rejection, `true` otherwise (the ratchet check is already synchronous at the top of that function; persistence stays async).
2. Accepted → `committedTier := target` locally, settle runs with `feelArmed = true`; at `onAnimationEnd`: fire the landing feel, `readoutTier := target`, lamp pulse.
3. Rejected → RETURNING settle to the committed stop, no feel, readout untouched. The existing ratchet banner says why, in words, once.
4. External change (loadout, sync) arriving in IDLE → silent settle (`feelArmed = false`), readout flips at its end. Arriving during PRESSED/DRAGGING/SETTLING → the external change wins: gesture cancelled silently, cap settles silent to the new tier. A rested bulk decision (the loadout) outranks a half-finished drag; the drag's author sees the cap leave and the readout tell the truth — nothing is hidden.

**Colour keyed to travel (TARGET — this replaces Round 0's discrete switch):**
Round 0 already keys the cap fill to `rockerIndex.roundToInt()` — the fill follows the cap, not the tap. The target removes the last step in it: **continuous RGB blend.** Stop fills `F0 = tokControlFill()`, `F1 = PEEK fill`, `F2 = tokSurfaceBright()`, cached as ints on mode/tier-paint setup (never allocated per frame):

- `pos ∈ [0,1]: fill = lerpRGB(F0, F1, pos)` · `pos ∈ [1,2]: fill = lerpRGB(F1, F2, pos−1)` — per-channel linear interpolation on packed ints in `onDraw`, three multiply-adds per channel, zero allocation across 12+ rows.
- PEEK fill: day `tokSecondaryContainer()` = #615B5A; night `tokSurfaceBright()` = **#362C27** (adopted from Schlitten's mail, superseding Round 0's interim demotion of night PEEK to `tokControlFill()`: the peach #FFB38F stays dead, but PEEK = SHUT's exact fill made the night blend invisible and collapsed two stops into one material). The night staircase is now three real steps: SHUT #4A423C (the fill cap) → PEEK #362C27 → OPEN hollow (below). Position remains the primary rendering; the fill now supports it at night instead of abdicating.
- **Night OPEN is hollow.** There is no fourth fill step available under the cap, so at night the PEEK→OPEN leg blends the fill #362C27 → the track recess colour across pos 1.0→2.0 while a 1.4dp outline in `tokTextSecondary()` (#9C8E84) fades in over pos 1.5→2.0; at rest the OPEN cap is the recess with a drawn edge — an empty frame, the silhouette of an open gate. Day OPEN stays the bright slab #FAF6F0. The lamp dash and the readout word are unchanged in both modes.
- **The halfway flip is dead.** There is no label inside the cap to flip and no discrete colour step at 0.5/1.5. The halfway crossings survive only as (a) the drag tick and (b) the release-target computation. The margin word flips on arrival (grammar outranks the shorthand).

**The three stops as pure position grammar — no words inside caps, ever.** The word loop drawn over the slots today (`SHUT / 2m / OPEN` printed on the track, selected word riding the cap) is **deleted**. In its place, the cap carries exactly one marker, and only at rest (`|animatedPosition − roundToInt| < 0.02`, or during the settle pulse): a cap in transit is blank, because a marker mid-flight would claim a stop it hasn't reached.

| Stop | Cap marker (at rest) | Lamp silhouette | Readout word (margin) |
|---|---|---|---|
| SHUT (0) | **vertical bar**, 3dp wide, round caps, centred, height −14dp, `tokOnControlFill()` cream — the sealed gate; same grammar as the loadout cap's line, so the family reads as one machine | **filled disc** Ø6dp, `tokTextPrimary()` | `SHUT` |
| PEEK 2M (1) | **horizontal slit**, 14dp wide × 2.5dp, centred — a two-minute window cut in the cap. Day: `tokOnSecondaryContainer()`; **night: cream `tokOnControlFill()` on the dark #4A423C cap** — position carries the state, the slit marks the exception, the lamp confirms | **ring** Ø6dp, stroke 1.4dp, `tokTextPrimary()`, hollow centre | `PEEK 2M` |
| OPEN (2) | **none** — day: a blank bright slab; night: the hollow frame (recess fill + #9C8E84 edge). Absence is the silhouette in both modes | **dash** 6dp × 1.6dp, `tokTextSecondary()` — the quietest glyph for the absence of guard | `OPEN` |

Disc / ring / dash are distinguishable in silhouette, in peripheral vision, and with colour removed entirely. The lamp and the word flip together, on arrival, with the lamp's 180ms settle pulse. THREE renderings of the tier exist — position, lamp, word — and that is not a one-fact violation: position is the state itself; lamp + word are the margin readout pair (glyph for the glance, word for the read), the same pairing the chrome uses everywhere. The slot words were a fourth rendering *and* a lying one; they are the ones that die.

**What the sled does not claim:** it renders the rested decision (this app's tier), nothing about live enforcement. Whether blocking is actually running right now is chrome/Guard territory; the sled never borrows that truth (no "proof" styling, no service-state tint). If the service is dead, that fact gets its own rendering in its own home — it does not leak into twelve rows.

---

## The list around the sleds

Host: `ShieldMatrixScreen.kt`, the `LazyColumn`. All numbers TARGET with current state named.

- **Rows: 52dp** (delta from 54dp), hairline under each, no row background. Sort: **grouped by state, alphabetical by app name inside each group** (delta: the DAO's `isBlocked DESC, interceptCount DESC` order is a ranking nobody chose for scanning; a rested user setting tiers scans by name — stable, predictable, zero ambiguity per row).
- **Real 28dp app icons** (delta: none exist today — the view draws a name and a pip, `getApplicationIcon` is called nowhere in the app). Host resolves `PackageManager.getApplicationIcon(packageName)` once per package into a small LruCache in the screen file and passes the `Drawable` into the view (`appIcon` property; the view draws the bitmap at 28dp, native shape, no imposed mask — recognition comes from the icon as the OS shows it). Load failure or not-yet-loaded → 28dp `tokSurfaceVariant()` box with the app's first two letters in Anonymous Pro; the box never changes size, so rows never shift.
- **Sticky search, 36dp** (delta: today it's a 40dp non-sticky `item{}`; container split adopted from Fenja's mail — she owns the search field). The section header + filter words form one scrolling `item{}`; the search field **alone** goes into a `stickyHeader`: 36dp visual height / 48dp touch height, recessed (`tokSurfaceVariant()` fill, no border, 13sp Atkinson real Bold, placeholder `Filter apps…`), on an opaque `tokBg()` strip whose bottom hairline shows only while stuck. When the query is non-empty, a `CLEAR` word (10sp Anonymous Pro, `tokTextSecondary()`, right inside the field) appears and clears on tap. The field never resizes; the keyboard appearing changes nothing above it. Row placement changes driven by filtering/search animate via `animateItem()` (CurveA, 260ms — Fenja Rung 3); group headers themselves appear/vanish instantly.
- **Filter words — settled form, kept exactly:** words + underline, no pills, no fills. `ALL` + the six `AppCategory` labels. Selected = ink (`tokTextPrimary()`, real Bold) + 2dp ink underline bar; unselected = `tokTextSecondary()` Regular. (Already shipped as `FilterWord` — KEEP, do not redesign.) Category filter composes *within* the state groups; a group emptied by the filter loses its header too.
- **Grouping by state:** groups in order **SHUT → PEEK 2M → OPEN**, each with a plain header item, 28dp: group word left (11sp Sora SemiBold, tracked, `tokTextPrimary()`), count at the right margin in Anonymous Pro (`7 APPS`). **OPEN is collapsed by default** — its header is 36dp (a full touch target) and carries the disclosure word at the right margin after the count: `SHOW` / `HIDE`. Collapse state is UI state only (`rememberSaveable` in composition; never persisted to DB — it is not a decision about apps, it's a posture of the list). Expand/collapse is instant.
- **Search flattens the groups.** With a non-empty query, group headers dissolve into one flat alphabetical list under a single `N RESULTS` header line — hunting one app by name must not mean scanning three state lists. Clearing the query restores the groups exactly as they were (OPEN's collapse state included).
- **Furniture budget:** within the apps section, non-row chrome in the first viewport = section header 20dp + filter-word row 24dp + gaps 16dp + sticky search 36dp + first group header 28dp = **124dp ≈ 19% of a 640dp viewport** — inside the ≤35% law with the loadout band and routines (content, not furniture) counted separately above it. The old `SHOWING N` readout in the section header **dies**: the group counts already say how many, where they belong (one fact, one rendering).
- **Deflection count — its Shield home (specced for either Ledger fate).** The apps-section header's right margin carries exactly one readout: **`47 DEFLECTED`** — Anonymous Pro numeral, Atkinson word, `tokTextSecondary()` — sourced from the existing `MonolithViewModel.totalInterceptions` flow (max of intercept-log size and Σ `interceptCount`). The label makes no time claim, because the source makes none: it is the all-time total, printed as a total. If the Ledger **lives**, it owns the itemised record and this header keeps the single summary total (summary at the point of decision, record in its home — the same split as Deck's `SHUT N APPS`). If the Ledger **dies**, this header is where the deflection truth lives on Shield, full stop — nothing else on the screen repeats it, and chrome keeps its existing SHUT-count badge, which is a different fact. This spec takes no position on the Ledger; the placement is identical either way, by design.

---

## Edge cases

- **Ratchet rejection mid-session** (weakening throw during an active session): synchronous `false` → silent 180ms CurveA return, readout and lamp untouched, no feel. The banner words already exist. Strengthening throws during a session work normally — the ratchet only refuses one direction, and the sled honours it without inventing a locked visual state (the row looks identical; only the throw's outcome differs, and the banner says why).
- **Alternative flagged, not adopted — Schlitten's sealed sled:** his peak makes the sled swallow touch mid-session and answer with one knock (pre-arm surface only). That contradicts the shipped product truth this spec is built on — `setAppBlockTier` is callable mid-session and the ratchet + banner exist precisely because rows stay live — and sealing is a behaviour decision above both our seats. Flagged here for Voss/Ren to settle at merge; if sealing wins, my ratchet-rejection path is deleted and the knock spec is his. Also flagged from his letter: his flick-to-end (≥800dp/s throws straight to the end stop) vs my +1-stop fling — on a 3-stop whose middle is a real destination, a hard flick from SHUT aimed at PEEK must not land on OPEN. Mine stands in this spec; merge decides.
- **Grab mid-settle:** DOWN cancels the animator at its current `animatedPosition`; grab offset recomputed from that position; the interrupted throw's feel is disarmed. No teleport, no double landing.
- **Double-tap / two sleds at once:** each view tracks one `activePointerId`; additional pointers are ignored; lift of a non-active pointer does nothing. Two rows are independent views — simultaneous throws on two rows each land with their own single feel (two acts, two sounds — truthful).
- **Finger exits the view mid-drag:** position clamps (with end resistance); UP outside horizontally still resolves by the clamped position. Detach mid-gesture or mid-settle cancels the animator; if the commit callback already fired, the state is true in the DB and only the garnish (feel/pulse) is lost — state truth never depends on an animation completing.
- **Rebind/recycling:** the Compose `update{}` block sets `currentTier` repeatedly; the setter must distinguish *rebind snap* (view newly bound or tier changed externally while IDLE → silent settle) from gesture commits, and must cancel any stale animator from a previous binding. A recycled view never inherits a previous app's pending feel.
- **Long names:** name is measured and ellipsised inside its zone; the readout word's 44dp zone and the track never move, shrink, or wrap. Tracks align in one column down the whole list, always.
- **Session active → effective night:** Shield already renders night while a session runs (`effectiveNight = isDarkTheme || isSessionActive`); the sled's paints key off `isNightMode` exactly as today — a throw across the session-start boundary re-caches stop fills, never blends day/night palettes mid-flight.
- **Empty states:** a group with no apps renders no header; a search with no matches renders one line in the list area — `NO APPS MATCH` (11sp, `tokTextSecondary()`) — and nothing else. No illustration, no orange.
- **Accessibility:** the sled view is focusable; `contentDescription` = `"<app name> — SHUT"` (readoutTier, so it never announces a mid-flight lie); three accessibility actions — *Set SHUT*, *Set PEEK 2M*, *Set OPEN* — each performing a programmatic throw **with** its landing feel (a deliberate act earns its feel, same as a tap). The name/icon region exposes no action. Reduced-motion (animator scale 0): settles snap, feel and readout flip still occur once.

---

## Code: helps / fights

**Helps (already true in code — build on them):**
- Round 0 keyed cap fill/label to `rockerIndex.roundToInt()` (*BlockitSuitcaseAndSwitchesViews.kt*, `AppRowSledToggleView.onDraw`) — the blend replaces that `when(capTier)` switch in the same slot; the animated-position plumbing already exists.
- `MechanicalOvershoot(1.25f)` and durations {180, 260, 280, 420, 520} exist in `MonolithCurves`; `CurveA` exists for returns. `animateRockerTo()` is the single animator to re-point.
- `fireFeel`'s contract is honest post-Round-0: `1L` is the only pitched path (`playRotaryDetent(freq)`), `4L` latch, `8L` firm click. The landing table routes entirely through it — no engine changes.
- `tokOnControlFill()` exists and is correctly cream in both modes (the night SHUT label pairing is already fixed); night PEEK fill already demoted to `tokControlFill()`.
- `FilterWord` (words + underline) is already shipped in `ShieldMatrixScreen.kt` — the settled filter form needs no work.
- `totalInterceptions` flow and `BlockedAppEntity.interceptCount` already exist — the deflection readout is a host binding, not new data.
- `SledSwitchView` in the same file already draws the track outline stroke this view lacks — the idiom is local.

**Fights (deltas the build must make):**
- `AppRowSledToggleView.onTouchEvent` is UP-only with lift-position slot picking → full gesture rewrite (DOWN/MOVE/CANCEL, grab offset, slop + scroll surrender, velocity, pointer id).
- The slot-word label loop in `onDraw` → deleted; margin readout word + 3-silhouette lamp + rest-only cap markers added. The binary pip (filled if SHUT else outline — PEEK and OPEN indistinguishable) → replaced by disc/ring/dash.
- `animateRockerTo()`: fixed 260ms CurveA → distance table + `MechanicalOvershoot` + end/cancel listeners carrying the feel/readout flip.
- `MonolithViewModel.setAppBlockTier()`: **delete `soundEngine.playRotaryDetent(freq)`** and **return Boolean** for ratchet acceptance. `cycleAppBlockTier()` / `toggleAppBlocked()` become dead with the name-tap — delete with it, don't leave a second tier path alive.
- `currentTier` setter animates on any external set → needs the silent/programmatic distinction (`feelArmed` flag + external-wins cancellation).
- `ShieldMatrixScreen`: row height 54→52dp; search 40dp `item{}` → 36dp `stickyHeader` with the filter row inside; flat `items(filteredApps)` → grouped items + OPEN collapse + flatten-on-search (host-side grouping/sort; DAO order bypassed); `SHOWING N` header readout → deflection readout; section horizontal padding 12→20dp (frozen margin).
- Icons: no icon pipeline exists anywhere — new host LruCache + `appIcon` property + bitmap draw in the view. The only genuinely new machinery in this spec, and it is ~30 lines.
- Sten's routine mini-sled (his Compose `RoutineRow` cap; the Canvas `SledSwitchView` is a separate binary control): throw contract mailed and answered. Agreed: one float drives offset + fill lerp + word (never twin animations), single sound owner, drag-past-halfway commits. **Held by me, pending merge:** the state word flips on arrival (his crossfade is a mid-flight flip — settled grammar, not seat-negotiable; his own 0.99-threshold fallback is the conforming form) and the settle seats with `MechanicalOvershoot` (his CurveA 260 has no seat — the hand must not learn two landings). Accepted as element-local: his VM-owned latch (routines have no rejection path, so commit-time sound is truthful there), tap-primary input, PAUSED as a third state parked OFF-ward (it will not fire, so OFF-ward is its truthful position).
- Note for the builder: wave-1's `04-sled-switch.md` on disk is truncated mid-§3; where it and this plan differ (discrete fill switch, halfway label behaviour), this plan governs.

---

## Law check

- **ARM ≤10s:** untouched — nothing here is on the arm path; a throw is ≤420ms and the list is rested-time work.
- **HOLD absolute:** mid-session, weakening throws are refused by the existing ratchet and the sled renders that refusal honestly — silent return + words in the banner. No new mid-session decisions are created; the name-tap's accidental ones are removed.
- **TELL THE TRUTH once, in human language:** one sound owner (the view, at landing); the VM's duplicate detent deleted; no feel for rejected, cancelled, or null acts; the readout word claims only the committed tier; the deflection number claims only what its source counts (a total, labelled as one).
- **One fact, one rendering:** slot words (4th rendering, and a lying one) die; `SHOWING N` dies where group counts live; deflection total has exactly one home on Shield; tier = position + margin readout pair.
- **State grammar:** position = power ✓ (fill is a function of position, never of intent); ink fill = selection ✓ (filter words); words at margins = readouts ✓ (readout word, group counts, deflection — none inside caps); everything answers on DOWN ✓ (press sink + drag on the DOWN frame); state words flip on arrival, never mid-flight ✓ (readout/lamp flip in `onAnimationEnd`); ripple: none exists in these Canvas views, none added.
- **Rested decides, tired executes:** the list is set while rested — hence drag with grab offset (no precision grip), generous touch rect, null-tap silence, scroll surrender, and zero ambiguity per row: one row, one app, one control, one word.
- **Beauty is retention:** the overshoot seat, the milled slot, the slit in the night PEEK cap — the physicality wave-1 diagnosed as missing, restored inside the existing token/curve/engine vocabulary. No new assets.
- **Locked items:** real font weights ✓ (Atkinson/Anonymous Pro real Bold files only); night staircase ✓ (ground #171412, fills ≤ #4A423C, text tiers respected, night SHUT = cream-on-cap via `tokOnControlFill()`, night PEEK = dark cap + cream slit); Shield accent budget ZERO orange ✓; never green ✓; banked hold — not this element, untouched; routines fully his — Sten's rows adopt the contract by mail, no routine behaviour is specced here; Guard/Ledger fates — **not decided here**; the deflection placement is specced to hold under either Ledger outcome.

— Yuki, Maximum-Implementable (sleds)
