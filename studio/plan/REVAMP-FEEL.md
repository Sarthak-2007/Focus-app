# REVAMP-FEEL — feel gaps, small things, and one sealed day traced

**Bundle:** FEEL + SMALL THINGS + FLOW for the launcher revamp
(branch `agent/design-revamp`, Slice 1 as built on disk).
**Read against:** `studio/plan/REVAMP-INTERACTION.md` §0 (normative),
`LauncherBlockScreen.kt` (incl. `SealHaptics`), `AirlockAndDisarmOverlays.kt`
(Airlock door chips), `MainActivity.kt` (launcher wiring), `MonolithCurves.kt`,
`MonolithCustomComponents.kt` (`BlockitActionChip`, header band).
**Excluded by order (in-flight fix-forward, judged as present, never
re-reported as gaps):** back law, stopwatch arc removal, safe-break
eyebrow, emergency break on grids, browser/store bars, airlock chip
timing, tap-time gates, safe-break peek. Where an on-disk remnant of
one of those lanes touches a finding below, it is flagged
*[adjacent: in-flight]* and not counted.
**Headline:** 33 feel gaps. Worst delta: the door plate — §0's
heaviest control (stiffness 260, damping 0.86, ≈85 ms to 90 % travel,
overshoot capped at scale 1.004) — ships with the bounciest spring in
the file (stiffness `Spring.StiffnessMedium` = 1500, damping 0.55):
the door is ~6× stiffer than spec and snappier than §0's *tile*
spring. The mass polarity of the whole launcher is inverted.

---

## PART 1 — FEEL: as-built vs §0

Architecture note that explains most rows: §0 Law 3 requires ONE
`pressProgress` float per control, spring-driven, lerping scale,
radius, elevation, translation and highlight together, with haptics
fired from that float crossing 0.35. The as-built has no
`pressProgress` anywhere. Each control animates a single uniform
`scale` via `animateFloatAsState`, snaps its shadow discretely with
`if (pressed) 1.dp else N.dp`, and fires haptics from click
callbacks. Every row below is a consequence or a sibling of that.

### Gap table

| # | Flow / control | §0 spec | As-built (code) | Delta | Fix |
|---|---|---|---|---|---|
| G-01 | Plate spring | `SpringSpec(0.86, 260)`, ≈85 ms to 90 %, settle ≈240/260 ms, overshoot ≤1.004 | `spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium /*1500*/)` in `LauncherDoorPlate` | Stiffness 5.8× spec, damping 0.31 low: a fast, visibly oscillating button-spring on the app's heaviest door | `LauncherMotion.HeavySpring` per §2.1; one spring both directions |
| G-02 | Plate squish | Non-uniform X 0.970 / Y 0.945 (−3.0 % / −5.5 %) | Uniform `scale(0.975f)` — one scalar, −2.5 % both axes | Reads as zoom, not weight; travel is also shallower than spec on both axes | Lerp scaleX/scaleY separately from `pressProgress` |
| G-03 | Plate radius | 20 dp → 27 dp morph (+7) from `pressProgress` | Fixed `RoundedCornerShape(18.dp)` — rest radius also 2 dp under spec | No morph; the cheapest mass cue (§1.1) unspent | Radius = `lerp(20.dp, 27.dp, p)` |
| G-04 | Plate shadow | Elevation 6 → 1 dp and shadow alpha 0.35 → 0.10, lerped on the same float | `.shadow(if (pressed) 1.dp else 6.dp)` — endpoints match, travel is a one-frame snap while scale springs over ~200 ms | Law 3 desync by construction: shadow teleports, face glides. The eye reads the snap as a glitch | Drive `shadowElevation` from the same animated float (graphicsLayer) |
| G-05 | Plate surface light | TranslationY +3.0 dp; top highlight α 0.16 → 0.03; bottom bevel +8 % darker | No translation, no highlight stroke, static 1 dp border in sheet colour | Plate scales in place; no "seats into the panel" cue | Add all three lerps to the plate's draw |
| G-06 | Plate commit beat | SEATED/inset held exactly 120 ms (elev. 0, radius held 27) before the grid transition | Click swaps `page` immediately; no seated state exists | The THUD has nothing to land on; press reads as delay-free teleport (§4.1's guarded number) | Hold SEATED 120 ms on commit, then transition |
| G-07 | Grid transitions | Open: content 14 dp → 0 + fade, 240 ms `EasingA` (= TAB_IN); close: 160 ms THROW | `when (page)` hard cut, both directions, no `AnimatedContent`/`AnimatedVisibility` in the file | Rooms snap; no spatial story HOME → grid | Animate page state with the spec's two tweens |
| G-08 | Plate geometry | Height 76 dp, margins 16 dp/side, gap 12 dp | Padding 18 dp vertical around 18 sp + 11.5 sp text ≈ 68 dp tall; host column pads 24 dp/side; gap spacer 14 dp | Door is ~8 dp short and ~8 dp narrower than spec; slightly airier gap | Set explicit 76 dp height, 16 dp margins, 12 dp gap |
| G-09 | Tile spring | `SpringSpec(0.58, 850)`, ≈55 ms to 90 %, release overshoot to 1.030 | `spring(0.5f, StiffnessMedium /*1500*/)` in `LauncherAppTile` | 1.8× too stiff; overshoot character wrong (ζ 0.50 rings longer, shallower) | `LauncherMotion.SnappySpring` per §2.1 |
| G-10 | Tile endpoints | Scale 0.920; elevation 4 → **0** dp (key bottoms out) | Scale 0.93; shadow floor `1.dp` | 1 pt shallow on scale; the key never bottoms out — a 1 dp shadow survives at full press | Match endpoints exactly |
| G-11 | Tile radius | Well 16 dp → 23 dp morph (+7) | Fixed 14 dp well / 16 dp cell clip, no morph | As G-03, on the frequent control where the cue repeats all day | Radius lerp from tile `pressProgress` |
| G-12 | Tile travel/light | TranslationY +2.0 dp; highlight α 0.12 → 0.02 | Neither exists | Tile shrinks in place instead of travelling down | Add both lerps |
| G-13 | OPEN tile commit | 1 dp accent edge (#F47442) lights 120 ms, then launch hand-off | Immediate `onTap` → launch; no edge, no beat | No confirmation frame between finger and another app | Light the edge, hold 120 ms, then dispatch |
| G-14 | PEEK tile commit | Tile goes SEATED/inset (recessed fill #100E0C, radius held) and STAYS inset while the Held Door raises over 320 ms | Tile tap closes the launcher instantly and routes to the Airlock; tile's last frame is unpressed | §0.2's promise-frame ("this one is being held for you") never appears | Seat the tile, raise the door over it, then cut |
| G-15 | Tile cell/label | Cell 84 dp fixed incl. label; icon 40 dp; label Atkinson 12 sp, **never truncated** (fewer columns before ellipsis) | Icon 40 dp ✓; label 10.5 sp, `maxLines = 2, Ellipsis`; cell height varies with 1- vs 2-line names | Names truncate (the exact failure §3.1 forbids); ragged row heights; label 1.5 sp small | 12 sp label, fixed 84 dp cell, single-line shrink-to-fit |
| G-16 | Haptic API surface | One object `LauncherHaptics` with 8 named flows: `enter/plateDown/plateLand/tileDown/openHandoff/peekRaise/back/sessionEnd` | `SealHaptics` with 3: `doorPress/sealWeight/tileSeat` — 5 of 8 flows have no function to call | The score is unbuildable as named; call sites name primitives' cousins, not flows | Rename/rebuild to the 8-function surface |
| G-17 | F-ENTER | CLICK 0.70, the frame the launcher surface becomes visible | `sealWeight()` = THUD 0.80, fired from `LaunchedEffect(visible, session?.id)` — post-composition, and re-fires whenever the session id changes under an open launcher | Wrong primitive (THUD is reserved for mass landing), wrong scale, wrong frame; also spends a dial-adjacent THUD at open against §0.3's "dial = 0 events" law | `enter()` CLICK 0.70 dispatched with the visibility change |
| G-18 | F-PLATE down-commit | CLICK 1.00 fired as `pressProgress` crosses 0.35 on the way DOWN | `doorPress()` = CLICK 0.90 fired inside the `onClick` lambda — i.e. on UP, after the full press duration | Same-frame law broken structurally: haptic lags the visual by the whole press (~100–250 ms), the exact "broken" feel find #2 names; scale also 0.10 under the reserved 1.00 | Fire from a `snapshotFlow { press.value }` 0.35-crossing collector (§2.1) |
| G-19 | F-PLATE land | THUD 0.60 at the grid-settle frame (240 ms) | Absent — there is no transition to land (G-07) | Flow spends 1 of its 2 scored events | Add with the G-07 transition |
| G-20 | F-OPEN | #1 CLICK 1.00 (tile down); #2 QUICK_FALL 0.80 at intent dispatch | `tileSeat()` = TICK 0.60 on tap; no hand-off event | Wrong primitive AND wrong class: TICK is §3.2's small-navigation voice — it now also means "app launched", so the grammar teaches two meanings for one sound. QUICK_FALL (departure) never heard anywhere | `tileDown()` CLICK 1.00 + `openHandoff()` QUICK_FALL 0.80 |
| G-21 | F-PEEK raise+seat | ONE composition: SLOW_RISE 0.70 @0 ms + THUD 0.80 @+300 ms, dispatched as the door raise starts — the launcher's only 3-event flow | Absent entirely; peek tap spends the same lone TICK 0.60 as OPEN | The most consequential door in the app feels identical to opening a calculator app | Build the composition; it is the budget's reason to exist |
| G-22 | F-BACK | LOW_TICK 0.70 (fallback TICK 0.50) the frame the close starts | Both back paths (`BackHandler`, `onBack`) set state with zero haptic calls | Navigation is feel-silent in one direction only — asymmetric grammar | `back()` on both paths |
| G-23 | F-END | ONE composition: QUICK_FALL 0.90 @0 + THUD 0.70 @+90 ms, the frame session-end lands | Absent — nothing in the launcher observes session end (see Part 3, step 6) | The session's last mechanical event is a disappearance | `sessionEnd()` wired to the end transition |
| G-24 | F-DENIED / DISABLED | Denied/empty target: **0 events by law**; a zero-app plate is FLAT — elev. 0, no highlight, no spring, no haptic | A plate with count 0 renders identically to a live one, springs (G-01) and spends CLICK 0.90, opening an empty room | The one denial the launcher can give *rewards* the tap with full feedback — trains tapping dead doors | Count 0 → FLAT rendering, no spring, no haptic, no navigation |
| G-25 | Fallback ladder | 3 rungs (COMP-30 / PREDEF-29 / WAVE-24), one rung per whole event, probed **once** per process | Gate requires CLICK+THUD+TICK supported *together*, else EVERY event collapses to `EFFECT_CLICK` (THUD/TICK voices lost); no WAVE rung — API 24–28 calls `createPredefined` (API 29+) inside `runCatching`, so haptics silently vanish; support probed inside every `compose()` call | Degradation is cliff-edged and silent on exactly the old devices the ladder ex
...[truncated 13805 chars]