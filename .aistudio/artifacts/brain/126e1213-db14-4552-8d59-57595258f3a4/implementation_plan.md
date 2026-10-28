# MONOLITH — 108 Hand-Drawn Custom Canvas & 3D Shader Controls, Zero Stock UI Widgets & Complete `DEVELOPER-GUIDE.pdf` Architecture

An original Android focus instrument and unbreakable application blocker engineered with **zero stock Material or Android UI widgets on any screen**. Every single surface, dial, numeral, switch, slider, row, badge, chart, receipt, keyboard input well, drawer, and overlay is constructed from **108 self-contained, hand-drawn custom Canvas & 3D WebGL controls** following the exact zero-allocation anatomy, 19-token resource discipline, `148dp/96dp` numeral architecture, 3-curve motion system, `1/4/8/10ms` one-shot haptics, and 4-partial Float-PCM synthesizer recipe from `DEVELOPER-GUIDE.pdf`.

---

## User Review & Complete Preservation of Every Instruction

> [!IMPORTANT]
> Every instruction you have ever given across all turns is preserved and synthesized into one cohesive specification:

1. **108 Hand-Drawn Custom Views & Controls — Zero Stock Android/Material UI Widgets**:
   - No `Material3` `Button`, `Card`, `Switch`, `Slider`, `Checkbox`, `OutlinedTextField`, `LinearProgressIndicator`, `ModalBottomSheet`, `AlertDialog`, `NavigationBar`, or `Icon` widgets appear on our designed screens.
   - All **108 custom controls** draw themselves directly on `Canvas` (plus our **3D Three.js WebGL Anisotropic Machined Ring Dial**) using pre-allocated `final Paint`, `final RectF`, and `final Path` fields with zero object allocation in `onDraw`, owned `ValueAnimator` instances (`Curves.A`, `Curves.B`, `Curves.C`), and self-contained touch/velocity/detent physics.
2. **All Learnings from `DEVELOPER-GUIDE.pdf` Applied End-to-End**:
   - **19 Color Tokens in `res/values/colors.xml` & `res/values-night/colors.xml` (§1)**: Zero hex literals in view or layout code; one saturated state accent (`colorPrimaryContainer`) used in $\le 3$ state-meaningful places per screen; flat by default with one raised tone (`colorSurfaceBright`) and one recessed tone (`colorSurfaceVariant`); hairline alpha (`0x75`) baked into `colorOutlineVariant`.
   - **Three Bundled Font Families & `148dp` Architectural Numerals (§2)**: Words never render in the number font; numbers never render in the word font; mixed strings split words (`PlusJakartaSans`) and quantities (`JetBrainsMono`) inside `onDraw`; hero countdown digits render at **`148dp` text size on a `96dp` column width** (`countdown_counter_text_size` / `countdown_counter_width_size` in `res/values/dimens.xml`); stencil/dot-matrix spice (`StencilMatrixFont`) used only for brand moments and counter tiles; zero italics anywhere.
   - **Strict Geometry in `res/values/dimens.xml` (§3)**: Two corner radii app-wide (`12dp` small tiles/chips/cells, `24dp` large viewports/sheets), one universal `12dp` spacer unit, fixed `80dp` custom bottom navigation bar, `48dp` cells (`28dp` glyphs), `14dp` slider dot radius, `4dp` dot-matrix tile grid, and **full-bleed edge-to-edge stacked sections** (zero floating margin cards).
   - **Three Motion Curves & 5 Distance-Governed Durations (§5)**: `Curves.A = PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f)`, `Curves.B = PathInterpolator(0.05f, 0.7f, 0.1f, 1.0f)`, `Curves.C = PathInterpolator(0.3f, 0.0f, 0.8f, 0.15f)` + `OvershootInterpolator`, with durations `{180, 260, 280, 420, 520}ms`.
   - **Haptics: Clicks, Not Buzzes (§6)**: `VibrationEffect.createOneShot(ms, DEFAULT_AMPLITUDE)` at strictly four durations (`1ms`, `4ms`, `8ms`, `10ms`)—zero `performHapticFeedback` calls anywhere.
   - **Sound: Single Float-PCM Hardware Synth, Zero Audio Files, Zero Ambient Sound Mixer (§7)**: Ambient sound mixer removed completely. A single dedicated `AudioTrack` writer thread synthesizes float PCM using partial weights `{ 0.6f, 0.28f, 0.37f, 2.2f }`, body decay `exp(-0.0114)` per sample step, and tail decay `exp(-0.0313)` per sample step, triggered at the exact same call site as the haptic pulse.
   - **Voice & Copy in `res/values/strings.xml` (§8)**: Short, concrete, ritual-flavored verbs (`arm`, `seal`, `bolt`, `commit`, `shut`) and plain nouns; zero exclamation marks, zero emoji, zero marketing adjectives.
3. **Confirmed Functional & UX Decisions Preserved 100%**:
   - **Single Unbreakable Vault Lock + 1 Weekly Emergency Break (60s Continuous Hold)**: No lock-tier selector; 10s accidental-start grace window; 1 Emergency Break per rolling 7-day week requiring a continuous **60-second hold** on our hand-drawn `Hold60sPressureValveView` + confirmation phrase; 100% unbreakable when `0/1` weekly tokens remain.
   - **Dedicated Active Lockdown Screen ("Sealed Vault Chamber")**: Starting a session transitions (`520ms`, `Curves.C`) into a dedicated dark obsidian Lockdown Chamber screen with the locked 3D anisotropic ring, `148dp` countdown numerals, Active Task & Sub-Step Execution Console, Safe Break cycle indicator, Sealed Shield telemetry, and Redacted Notification Vault.
   - **One-Way Ratchet Shield Enforcement**: During an active session or routine, users can tighten rules (lock more apps, lower daily quotas, enable Shorts Sniper / Web Shield), but cannot unlock any `Shut` app or disable an active schedule until `00:00`.
   - **Every Previously Built Feature Preserved**:
     - 5 Study Presets (`15m`, `25m`, `45m`, `90m`, `Open Flow` stopwatch with 20m floor) & Pomodoro block multiplier (`1×..4×`)
     - Dedicated Active Task panel, checkable micro-steps (`TaskSubStep`), session checklist (`StudyChecklistItem`), and `+5m Overtime`
     - Horizontal Landscape Nightstand Desk Clock (`AlwaysOnDeskClockOverlay`) with Figure-8 OLED burn-in drift
     - Pull-Down Unified Status & System Guard Drawer from the top instrument header (5 OS protection layers, Shorts/Reels Sniper, Browser Social URL Shield, and Held Notifications Digest)
     - Edge-to-Edge `80dp` Custom Bottom Hardware Bar (`FOCUS | SHIELD | TASKS | HORIZON`)
     - 3 Swipeable Smart Loadout Cards (`Deep Study`, `Work Sprint`, `Full Lockdown`), Category Filter Strip, Custom Search Well, Per-App 3-Stop Sled (`Shut | 2m Knock | Open`), and Per-App Daily Usage Quota Track
     - Visual 24-Hour Draggable Auto-Routines (`00:00–24:00`) + 7 `M–S` Day Toggles inside the `Shield` tab (`Installed Apps | Auto-Routines` switch)
     - Task Sequencer with Total Workload Horizon Bar, Single-Line Quick-Add, Proportional Single-Line Task Bands, `↑/↓` Priority Reorder, `▶ Cue to Timer`, `Defer`, and 5s Undo Delete
     - Horizon Analytics with Inline 7-Day Consistency Strip + Scrollable `TowerView` (7 bricks per floor), Stacked Focus + Deflection Bar Chart above a single baseline, and Full-Width Cylindrical Horizontal Thumb Drum Roller
     - 6 Interactive Kinetic Curio Artifacts (`Harmonic Loop`, `Brass Gyroscope`, `Hyperbolic Loom`, `Kepler Astrolabe`, `Acoustic Pendulum`, `Obsidian Tesseract`) + `FallingSquaresView` 16-square milestone burst
     - Perforated Thermal Session Receipt Card with side punch notches, dashed tear line, generative cadence barcode, stamp motifs, and HMAC verification + Encrypted State Backup
     - 5-Second Volumetric Breathing Chamber Airlock Interception Wall + Floating 2-Minute Peek Countdown Pill (for `2m Knock` apps outside strict sessions).

---

## 1. The 19 Zero-Green Color Tokens (`res/values/colors.xml` & `res/values-night/colors.xml`)

Every custom view resolves its `Paint` colors once in its constructor/initializer from these 19 resource slots. Every single token satisfies the strict mathematical Zero-Green invariant (`G < max(R, B)`):

| # | Resource Token Name | Light Mode (`res/values/colors.xml`) | Night / Sealed Vault (`res/values-night/colors.xml`) | Structural Role |
| :--- | :--- | :--- | :--- | :--- |
| 1 | `colorBackground` | `#F4EFE8` (Warm Porcelain Ground) | `#16131A` (Deep Obsidian Ground) | Window & full-bleed screen ground |
| 2 | `colorSurface` | `#F4EFE8` (Warm Porcelain Ground) | `#16131A` (Deep Obsidian Ground) | Default full-bleed band surface (flat by default) |
| 3 | `colorSurfaceSheet` | `#F4EFE8` (Warm Porcelain Ground) | `#211D26` (Obsidian Sheet) | Custom drawers, sheets, and modal plates |
| 4 | `colorSurfaceVariant` | `#E6DFD6` (Recessed Stone, $-5$ L) | `#1D1922` (Recessed Carbon) | Recessed tracks, preset tiles, inset wells |
| 5 | `colorSurfaceBright` | `#FAF6F0` (Sunlit Ceramic) | `#2B2533` (Anodized Titanium) | The raised hero dial viewport & active task console |
| 6 | `colorPrimary` | `#1C181A` (Deep Carbon Ink) | `#F7EAE2` (Warm Cream Ink — inverts, never greys) | Primary controls, active buttons, ink fills |
| 7 | `colorOnPrimary` | `#FAF6F0` (Sunlit Ceramic) | `#16131A` (Deep Obsidian) | Content drawn on `colorPrimary` |
| 8 | `colorPrimaryContainer` | `#E8451E` (Burnt Vermilion) | `#F46036` (Luminous Vermilion) | **THE single saturated accent** — spent on session timeline bar, active sled/power tiles, and armed state ($\le 3$ per screen) |
| 9 | `colorOnPrimaryContainer` | `#3D0E02` (Deep Vermilion Ink) | `#1C0702` (Dark Espresso) | Content drawn on `colorPrimaryContainer` |
| 10 | `colorSecondaryContainer` | `#5E5558` (Warm Graphite Pill) | `#FFB094` (Warm Rose-Amber Pill) | Selected pill in the `80dp` bottom navigation bar |
| 11 | `colorOnSecondaryContainer` | `#F7EFEA` (Warm Alabaster) | `#3B2822` (Deep Umber) | Content on the bottom navigation pill |
| 12 | `colorOnSurface` | `#3D3638` (Primary Glyph Ink) | `#F5E8E0` (Warm Cream Glyph) | Primary hand-drawn vector glyphs on surfaces |
| 13 | `colorOnSurfaceVariant` | `#786E70` (Secondary Pewter) | `#D9C5B8` (Secondary Warm Silver) | Secondary text, tick marks, and inactive glyphs |
| 14 | `colorOutline` | `#5E5558` (Strong Border) | `#857A7D` (Titanium Outline) | Strong structural borders and lever rims |
| 15 | `colorOutlineVariant` | `#755E5558` (`0x75` Alpha) | `#758F8487` (`0x75` Alpha) | Universal `1dp` full-bleed section hairlines |
| 16 | `blockBarSurface` | `#241E22` (Darkest Chamber Bar) | `#0E0B10` (Abyssal Vault Bar) | The session/commit bar & top pull-down header |
| 17 | `textColorPrimary` | `#241F21` (Primary Body Ink) | `#F8F2F0` (Crisp Alabaster Text) | Primary labels and headings |
| 18 | `textColorSecondary` | `#5E5558` (Secondary Slate Ink) | `#EDE0DC` (Soft Alabaster Text) | Supporting captions and metadata |
| 19 | `nothing_red` | `#D90B1C` (Matrix Stencil Crimson) | `#FF2A3C` (Luminous Matrix Red) | Brand dot-matrix readouts & pilot lamp core only |

---

## 2. Typography, Geometry & Resource Architecture (`res/values/dimens.xml`, `res/font/`, `res/values/strings.xml`)

### 2.1 Three Font Resources (`res/font/`)
1. **`R.font.plus_jakarta_sans` & `R.font.plus_jakarta_sans_bold`**: Used for **all words** (labels, buttons, headings, app names, task titles). Never renders a numeral.
2. **`R.font.space_mono_regular` / `R.font.jetbrains_mono`**: Used for **every number** (timers, counts, quotas, stats, percentages). In every custom view's `onDraw`, mixed strings (such as `"12 APPS SHUT"` or `"25m SPRINT"`) split the numeral and word into two `canvas.drawText(...)` calls using `paintNumberMono` and `paintWordSans`.
3. **`R.font.space_grotesk_bold` (with 4dp Dot-Matrix Tile Renderer `TileNumberView`)**: Used for `148dp` hero countdown digits and `4dp`-grid dot-matrix counter readouts. Zero italic styles exist anywhere in the codebase.

### 2.2 Named Dimensions (`res/values/dimens.xml`)
Zero magic numbers in layouts or views; every custom view loads these dimensions once in `init`:
- `corner_radius_small = 12dp` (Tiles, chips, buttons, sled switches, cells)
- `corner_radius_large = 24dp` (Hero dial card, sheets, session receipt card)
- `content_gap_unit = 12dp` (Universal spacer — all internal paddings are multiples of `12dp`)
- `bottom_nav_height = 80dp` (Fixed custom bottom hardware bar)
- `machine_commit_height = 72dp` (Full-width `SlideToStartView` / `Hold60sPressureValveView`)
- `bag_app_cell_size = 48dp` (`28dp` hand-drawn glyph inside `48dp` cell)
- `slider_dot_radius = 14dp` (Rotary ring & timeline handles)
- `dot_matrix_tile_size = 4dp` (`TileNumberView` counter machine grid)
- `countdown_counter_text_size = 148dp` / `countdown_counter_width_size = 96dp` (Architectural hero countdown digits)

---

## 3. Complete Catalog of 108 Hand-Drawn Custom Views & Canvas Controls (Zero Stock Widgets)

Every control below is a self-contained custom-drawn component with pre-allocated `final Paint`, `final RectF`, `final Path`, owned `ValueAnimator` (`Curves.A`, `Curves.B`, `Curves.C`), owned `Vibrator` (`1/4/8/10ms` one-shot), and synchronized `SoundSynth` trigger.

```
┌──────────────────────────────────────────────────────────────────────────────┐
│           THE §4.1 ZERO-ALLOCATION CUSTOM VIEW ANATOMY (x108 VIEWS)          │
│                                                                              │
│  1. Preallocated Final Fields:                                               │
│     • final Paint paintSurface, paintRecessed, paintInk, paintAccent         │
│     • final Paint paintHairline, paintWordSans, paintNumberMono              │
│     • final RectF boundsRect, thumbRect, arcRect                             │
│     • final Path clipPath, glyphPath, vernierPath                            │
│  2. Self-Owned Feel & Physics Engine:                                        │
│     • final ValueAnimator settleAnimator (Curves.A / Curves.B / Curves.C)    │
│     • final MonolithFeelEngine feel (Vibrator 1/4/8/10ms + Float-PCM Synth)  │
│  3. Zero-Allocation onDraw(Canvas):                                          │
│     • Primitives only: drawRoundRect, drawArc, drawLine, drawPath, drawText  │
│     • Pre-built readout strings updated only when state mutates              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Family A: Global Chrome, Navigation & Pull-Down System Drawer (Views 001–014)
1. **`MonolithPullDownBarView` (001)**: Top `48dp` full-bleed `blockBarSurface` instrument bar with machined center grab groove, downward drag velocity tracker, and status slots.
2. **`JewelPilotLampView` (002)**: Hand-drawn `10dp` obsidian bezel, radial glow ring, `nothing_red` / `colorPrimaryContainer` core, and top-left specular dot.
3. **`TileNumberView` (003)**: The `4dp` dot-matrix "counter machine" numeral renderer (`5×7` tile matrix drawn via pre-allocated `RectF` grid) for streak and score counters.
4. **`HeldPingCounterPillView` (004)**: Hand-drawn envelope/bell vector glyph + `Space Mono` counter capsule in the top bar; pulses on capture.
5. **`GuardReadinessMeterPillView` (005)**: 4-segment micro charge bar (`X/4 ARMED`) drawn on Canvas in the top right of the header.
6. **`BottomHardwareNavView` (006)**: Fixed `80dp` full-bleed bottom bar with 4 compartments (`FOCUS`, `SHIELD`, `TASKS`, `HORIZON`), etched vertical hairlines (`colorOutlineVariant`), and a sliding `colorSecondaryContainer` pill (`Curves.A`, `260ms`) firing `4ms` click + synth.
7. **`NavGlyphFocusView` (007)**: Hand-drawn concentric chronometer reticle vector path with animated phase sweep when selected.
8. **`NavGlyphShieldView` (008)**: Hand-drawn geometric vault-shield vector path with sliding internal bolt line.
9. **`NavGlyphTasksView` (009)**: Hand-drawn proportional 3-bar sequence glyph with animated check tick.
10. **`NavGlyphHorizonView` (010)**: Hand-drawn stacked column + vernier wave glyph.
11. **`UnifiedStatusSheetView` (011)**: Custom pull-down spring sheet (`Curves.C`, `420ms`) with backdrop scrim and zero Material sheet dependencies.
12. **`SledSwitchView` (012)**: Mechanical toggle sled sliding inside a `12dp`-radius `colorSurfaceVariant` track with lever throw (`260ms`, `Curves.A`) and `8ms` firm click + `420Hz` synth.
13. **`AdvanceLeverView` (013)**: Heavy industrial throw lever with knurled grip grooves for global `Shorts Sniper` and `Browser URL Shield`.
14. **`RedactedPingStampView` (014)**: Hand-drawn classified/redacted stencil bar (`[SEALED UNTIL 00:00]`) hiding held notification text during active sessions.

### Family B: Focus Deck — Idle Setup & 3D Anisotropic Ring Dial (Views 015–032)
15. **`ThreeJsMachinedRingView` (015)**: Hardware-accelerated **3D Three.js WebGL Precision Machined Ring Dial** (`THREE.TorusGeometry` + custom GLSL anisotropic brushed titanium shader + radial vernier ticks + angular touch raycasting).
16. **`RotaryWheelCanvasView` (016)**: Zero-allocation native `Canvas` companion ring dial (`RotaryWheel` pattern: 36 radial detents for `5m..180m`, velocity fling, `Curves.B` `280ms` settle, `4ms` detent tick + `640Hz` synth) synchronized with the 3D shader.
17. **`HeroArchitecturalTimerView` (017)**: Draws the **`148dp` countdown numerals on `96dp` column width** (`countdown_counter_text_size` / `countdown_counter_width_size`) with colon pulse and zero horizontal jitter.
18. **`FinishArrivalBadgeView` (018)**: Hand-drawn capsule computing and rendering `ENDS 15:45` in split `PlusJakartaSans` + `SpaceMono`.
19. **`HapticPresetChipView` (019)**: The 74-line `HapticPreset` archetype (`12dp` radius, `PlusJakartaSans` label + `SpaceMono` numeral, press scale `0.95f` over `180ms` `Curves.A`, `4ms` click, selected = `colorPrimary` or `colorPrimaryContainer` fill) instantiated for `15m`, `25m`, `45m`, `90m`, and `Open Flow`.
20. **`RombiKnobView` (020)**: Diamond/rhombus rotary selector (`RombiKnob` pattern) for selecting Pomodoro study block multipliers (`1×`, `2×`, `3×`, `4×` blocks) with `8ms` detent landings.
21. **`HorizontalVolumetricRibbonView` (021)**: Full-width horizontal time ribbon (`TimelineBar` pattern) with segment ticks, `12dp` rounded ends, `colorOutlineVariant` hairline track, `colorPrimaryContainer` fill, and diagonal hatch lines.
22. **`VernierScaleRulerView` (022)**: 3-tier calibrated ruler (`5m` minor, `15m` medium, `30m` major ticks + `SpaceMono` labels) drawn beneath the time ribbon.
23. **`ActiveIntentionBandView` (023)**: Full-bleed stacked band displaying the `#1 ON STAGE` task title, sub-step fraction pill (`2/3`), and one-tap `+5m` chip.
24. **`SlideToStartView` (024)**: Full-width physical commit track (`SlideToStart` pattern: knurled `colorPrimaryContainer` thumb dragged full width to commit; partial drags spring back with `OvershootInterpolator`; completion fires `10ms` clunk + `185Hz` bolt synth).
25. **`DeskMonitorTriggerTileView` (025)**: Hand-drawn `[⛶ DESK]` aspect-frame button launching the landscape nightstand monitor.
26. **`VaultReadinessSummaryBarView` (026)**: Pre-commit readout strip drawing `N APPS SHUT • 1/1 WEEKLY BREAK` in split sans/mono type.
27. **`CornerCalibrationCrosshairView` (027)**: Draws 4 precision rangefinder corner reticles inside the `colorSurfaceBright` hero viewport.
28. **`KnurledThumbRenderer` (028)**: Reusable `Canvas` primitive painting 3 debossed vertical grip grooves + specular edges on every draggable thumb.
29. **`RecessedWellFrameView` (029)**: Draws an inset `colorSurfaceVariant` trough with `1dp` `colorOutlineVariant` hairline border.
30. **`InlineTextEditorWellView` (030)**: Custom Canvas-drawn text input well with blinking `2dp` `colorPrimaryContainer` cursor and custom focus border (replacing Material `OutlinedTextField`).
31. **`PomodoroCycleDotsView` (031)**: Hand-drawn row of 4 mechanical cycle sockets showing `Work` vs `Break` intervals.
32. **`PreFlightPrimerSheetView` (032)**: Custom Canvas modal plate explaining Accessibility & Notification shield readiness with a `SlideToStartView` commit bar.

### Family C: Dedicated Active Lockdown Screen — "Sealed Vault Chamber" (Views 033–048)
33. **`SealedVaultChamberView` (033)**: Full-screen Night-token (`#16131A`) container that takes over the Focus viewport (`520ms`, `Curves.C`) the moment a session is armed.
34. **`LockedAnisotropicChronometerView` (034)**: Locked state of the 3D Three.js ring + `148dp` countdown numerals where touch cannot shorten the timer and the outer containment bezel clamps shut.
35. **`GraceWindowCountdownBarView` (035)**: High-visibility 10-second draining bar (`10.0s .. 0.0s`) allowing accidental-start abort during the first 10 seconds only, before bolting the vault shut.
36. **`VaultBoltSealHeaderView` (036)**: Top full-bleed `blockBarSurface` lockdown banner with pulsing `nothing_red` pilot lamp and `VAULT SEALED • BLOCK 1/2` readout.
37. **`ActiveTaskExecutionConsoleView` (037)**: Full-bleed execution console inside the Sealed Vault Chamber displaying the active task, checkable micro-steps, and study checklist.
38. **`MechanicalLatchCheckboxView` (038)**: `26dp × 26dp` `8dp`-radius custom Canvas checkbox that snaps shut with an `8ms` firm click and animated vector checkmark (`Curves.A`, `180ms`).
39. **`MicroStepExecutionRowView` (039)**: Hand-drawn sub-step row (`[x] Sketch diagram & list variables`) with strikethrough sweep animation on completion.
40. **`StudyChecklistBandView` (040)**: Compact 3-item session readiness checklist (`Phone on Silent`, `Water Ready`, `Notes Open`) drawn as a horizontal strip of latch tiles.
41. **`OvertimeExtensionKeyView` (041)**: Tactile `[+5M OVERTIME]` custom Canvas button that adds 300 seconds to the active task and session ring with an `8ms` click.
42. **`CompleteAndAdvanceKeyView` (042)**: Primary ink `[✓ COMPLETE & NEXT]` button that marks the current task done and loads the next queued task without breaking the vault seal.
43. **`SafeBreakChamberBannerView` (043)**: Rest-phase indicator band showing `SAFE BREAK • 04:59` with breathing pulse between Pomodoro blocks.
44. **`SealedShieldTelemetryStripView` (044)**: Live 3-column hardware counter strip (`SHUT APPS`, `DEFLECTED REACHES`, `REDACTED PINGS`) + `[TEST AIRLOCK]` button.
45. **`WeeklyBreakTokenBadgeView` (045)**: Renders the `1/1 WEEKLY EMERGENCY BREAK` token status as a physical metal fuse icon (intact vs blown/spent).
46. **`Hold60sPressureValveView` (046)**: The **60-Second Continuous Hold Emergency Break Control**—a full-width pressure bar that fills over `60,000ms` while held, firing a 1Hz heartbeat tick (`4ms` haptic + synth) and snapping back to `0%` (`Curves.A`, `260ms`) if the finger lifts for even a millisecond.
47. **`CipherPhrasePlateView` (047)**: Hand-drawn cryptographic reference plate + custom character-by-character verification track (`I AM STOPPING EARLY ON PURPOSE`) required alongside the 60s hold.
48. **`UnbreakableLockoutPlateView` (048)**: Displayed when `weeklyEmergencyTokens == 0`: a sealed titanium plate reading `0/1 WEEKLY BREAKS LEFT • VAULT UNBREAKABLE UNTIL 00:00`.

### Family D: Shield Matrix, One-Way Ratchet & 24h Auto-Routines (Views 049–066)
49. **`TwoStopModeRockerView` (049)**: Top full-bleed segmented switch (`INSTALLED APPS` | `AUTO-ROUTINES`) with sliding ink pill (`260ms`, `Curves.A`).
50. **`RatchetLockBannerView` (050)**: Full-bleed warning strip (`ONE-WAY RATCHET ACTIVE • CAN LOCK MORE, CANNOT UNLOCK`) visible whenever a session or routine is running.
51. **`LoadoutDeckPagerView` (051)**: Custom horizontal strip of 3 `LoadoutTileView` modules (`Deep Study`, `Work Sprint`, `Full Lockdown`).
52. **`LoadoutTileView` (052)**: Hand-drawn `12dp`-radius preset card displaying loadout name, `SpaceMono` shut-app count, and active `colorPrimaryContainer` indicator bar.
53. **`BagTilesGridView` (053)**: The `BagTiles` pattern from `DEVELOPER-GUIDE.pdf` §4.2—a single-Canvas grid of `48dp` cells (`28dp` custom app glyphs) previewing the apps sealed inside the active loadout; empty cells render as `colorOutlineVariant` slots; tapping opens the custom app picker.
54. **`CategoryFilterStripView` (054)**: Horizontal scrollable row of custom `HapticPreset`-style filter chips (`All`, `Video`, `Social`, `Games`, `Chat`, `Web`, `Tools`).
55. **`CustomSearchWellView` (055)**: Hand-drawn recessed search bar with vector magnifier glyph, live filter text, `[✕]` clear tile, and `[+ APP]` trigger.
56. **`AppRuleBandView` (056)**: Full-bleed `56dp` single-Canvas row drawing the custom `28dp` category glyph, app name (`PlusJakartaSans`), usage/quota readout (`SpaceMono`), and embedded 3-stop sled switch—zero child View inflation!
57. **`ThreeStopSledSwitchView` (057)**: Custom 3-position mechanical sled (`Shut | 2m Knock | Open`) with `260ms` `Curves.A` throw and **One-Way Ratchet Lock** (rejects leftward/loosening throws when `isRatchetLocked == true` with a double `10ms` clunk).
58. **`DailyQuotaScrubberView` (058)**: Expandable horizontal quota track (`15m .. 120m` daily limit vs `todayUsageMinutes` fill bar) with `14dp`-radius dot thumb (`slider_dot_radius`).
59. **`AppIconGlyphCanvas` (059)**: Procedural vector icon renderer drawing crisp geometric silhouettes for Instagram, TikTok, YouTube, X, Reddit, WhatsApp, Discord, Chrome, and Games on `Canvas`.
60. **`Routine24hTimelineView` (060)**: Full-bleed 24-hour (`00:00–24:00`) recessed timeline bar with hour ticks and dual `14dp`-radius draggable Start/End handles (`4ms` detent per 30m step).
61. **`DayOfWeekSevenCellView` (061)**: Single-Canvas 7-cell strip (`M T W T F S S`) where each `48dp` cell toggles with an `8ms` click and `colorPrimary` / `colorPrimaryContainer` fill.
62. **`RoutineHeaderLeverBandView` (062)**: Header band for each Auto-Routine displaying routine title, `SpaceMono` window (`09:00–17:00`), and ratchet-protected `SledSwitchView`.
63. **`CustomAppCreatorDialogView` (063)**: Hand-drawn modal plate for registering a new package name + category + initial block tier without stock `AlertDialog`.
64. **`RoutineCreatorDialogView` (064)**: Hand-drawn modal plate for creating a new scheduled profile with custom time window.
65. **`UrlDomainShieldBandView` (065)**: Full-bleed inspection strip showing blocked browser domains (`instagram.com`, `tiktok.com`, `reddit.com`, `youtube.com/shorts`) and allowed study domains (`scholar.google.com`, `arxiv.org`).
66. **`BulkCategorySealBarView` (066)**: One-tap commit bar to seal all apps in the currently selected category (`SEAL ALL VIDEO & SOCIAL`).

### Family E: Task Sequencer & Proportional Horizon Queue (Views 067–079)
67. **`TotalWorkloadHorizonView` (067)**: Full-bleed top capacity bar (`TimelineBar` pattern) rendering total queued minutes (`95m TOTAL • 3 TASKS`) and proportional segmented blocks for every queued task.
68. **`QuickAddTaskModuleView` (068)**: Full-bleed quick-add composer combining `InlineTextEditorWellView`, 4 `HapticPresetChipView` duration tiles (`15m`, `25m`, `45m`, `60m`), and an ink `[+ ADD]` tile.
69. **`ProportionalTaskBandView` (069)**: Single-Canvas full-bleed task band whose internal duration meter bar and vertical height scale proportionally to `estimatedMinutes` (`15m..60m`), containing latch checkbox, title, tappable `SpaceMono` duration chip, and action tiles.
70. **`PriorityOrderKeyPairView` (070)**: Paired `[↑]` and `[↓]` `48dp` mechanical key tiles firing `4ms` clicks to reorder queue priority.
71. **`CueToTimerKeyView` (071)**: High-contrast `[▶ CUE]` tile that loads the task into `#1 ON STAGE`, sets the 3D ring dial to `task.estimatedMinutes`, fires `8ms` click + synth, and navigates to `FOCUS`.
72. **`TaskSubStepDrawerView` (072)**: Expandable full-bleed sub-band rendering `TaskSubStep` items, progress bar, inline step input, and `[AUTO-SPLIT 3 STEPS]` generator tile.
73. **`DeferTaskKeyView` (073)**: Hand-drawn `[⏸ DEFER]` tile moving a task between active queue and deferred backlog.
74. **`DeleteTaskKeyView` (074)**: Hand-drawn `[✕]` delete tile (locked/disabled on the `#1 Active` task during a sealed session).
75. **`UndoDeleteToastBarView` (075)**: Pinned 5-second draining progress bar at the bottom of `TASKS` with one-tap `[UNDO]` recovery.
76. **`CompletedTasksArchiveBandView` (076)**: Collapsible full-bleed footer band showing completed task count, `[RESTORE ↺]` tiles, and `[CLEAR COMPLETED]`.
77. **`DeferredBacklogBandView` (077)**: Collapsible full-bleed band for saved-for-later tasks with one-tap `[PROMOTE ↑]`.
78. **`EmptyTaskQueuePlateView` (078)**: Hand-drawn architectural empty state (`0 QUEUED • HORIZON CLEAR`) with quick-seed buttons.
79. **`TaskDurationCycleChipView` (079)**: Tappable `SpaceMono` badge (`15m -> 25m -> 45m -> 60m`) that cycles a task's duration in place with a `4ms` click.

### Family F: Horizon Analytics, Tower Accumulation & Curio Vault (Views 080–095)
80. **`SevenDayFaderStripView` (080)**: The `DayFaders` pattern from `DEVELOPER-GUIDE.pdf` §4.2—7 vertical fader cells (`MON..SUN`) drawn on one Canvas with `SpaceMono` minute readouts and active `colorPrimaryContainer` fills.
81. **`TowerAccumulationView` (081)**: The `TowerView` pattern from `DEVELOPER-GUIDE.pdf` §4.2—physical accumulation bricks laid **7 per floor** (`floor = (days + 6) / 7`), one brick per focus block/day, scrollable and drawn with zero child views.
82. **`StackedHorizonChartView` (082)**: Zero-allocation custom Canvas chart rendering stacked **Focus Minutes (`colorPrimaryContainer`)** and **Deflected Reaches (`colorPrimary`)** above a single baseline with `30m / 60m / 90m` dashed hairlines and scrubbed crosshair column.
83. **`HorizontalThumbDrumView` (083)**: Full-width cylindrical vernier thumb roller (`sin(theta)` / `cos(theta)` foreshortened ticks) spanning the bottom of the chart, firing `1ms` fine ticks and `4ms` slot detents.
84. **`GranularityThreeStopView` (084)**: Custom 3-stop switch (`DAY | MONTH | YEAR`) with `< PERIOD >` stepper arrows in the chart header.
85. **`ChartTelemetryReadoutWellView` (085)**: Recessed readout strip above the chart displaying the scrubbed slot's date, `SpaceMono` focus minutes, and deflected impulse count.
86. **`FallingSquaresCelebrationView` (086)**: The `FallingSquaresView` pattern from `DEVELOPER-GUIDE.pdf` §4.2—a one-shot celebration rain of 16 geometric squares triggered on session completion and artifact unlock milestones.
87. **`CurioArtifactSelectorGridView` (087)**: 6-tile grid of `12dp`-radius hardware sockets for the 6 Kinetic Artifacts (`Toy 01`..`Toy 06`), showing lock thresholds (`25m`, `45m`, `90m`, `150m`, `240m`, `360m`).
88. **`LissajousCoreCanvasView` (088)**: Interactive Toy 01 (`Harmonic Loop`)—touch-driven phase/frequency wave spinner drawn with pre-allocated `Path`.
89. **`TorsionGyroCanvasView` (089)**: Interactive Toy 02 (`Brass Gyroscope`)—4 concentric 3D-projected gimbal rings tilting under finger drag.
90. **`HyperbolicLoomCanvasView` (090)**: Interactive Toy 03 (`Hyperbolic Loom`)—parabolic string-art envelope generator responding to touch coordinates.
91. **`KeplerAstrolabeCanvasView` (091)**: Interactive Toy 04 (`Kepler Astrolabe`)—epicyclic orbital gear train with draggable time-scale velocity.
92. **`AcousticPendulumCanvasView` (092)**: Interactive Toy 05 (`Acoustic Pendulum`)—12-bob coupled Bowditch pendulum wave harmonograph.
93. **`ObsidianTesseractCanvasView` (093)**: Interactive Toy 06 (`Obsidian Tesseract`)—4D hypercube stereographic projection rotated in $XW/YW$ planes by touch drag.
94. **`SerialPlateEasterEggView` (094)**: Anodized serial number plate (`MONOLITH // REV 2.4`) that unlocks the hidden harmonic synth mode after 5 rapid taps (`4ms` clicks).
95. **`EncryptedBackupPlateView` (095)**: Hand-drawn `HmacSHA256` state backup inspector with one-tap clipboard export tile.

### Family G: Overlays, Airlock Wall, Receipts & Nightstand Clock (Views 096–108)
96. **`AirlockBreathingChamberView` (096)**: Full-screen interception wall featuring a **5-second expanding/contracting volumetric breathing chamber** (`Curves.C`) that must complete before revealing the return commit track.
97. **`ImpulseReasonSelectorGridView` (097)**: 4 custom Canvas reason tiles (`Opened by Habit`, `Stuck on Hard Step`, `Low Energy`, `Urgent Message`) with dynamic coaching banner.
98. **`MicroStepSplitCommitView` (098)**: Inline 10-minute task splitter inside the Airlock wall when `Stuck on Hard Step` is selected.
99. **`FloatingPeekPillCanvasView` (099)**: Edge-snapping draggable floating `2m Knock` countdown capsule (used only outside strict focus sessions when a `2m Knock` app is peeked).
100. **`LandscapeNightstandClockView` (100)**: Full-screen horizontal desk monitor (`AlwaysOnDeskClockOverlay`) rendering `148dp` tabular numerals, full-width progress bar, active task ticker, and Figure-8 OLED burn-in drift.
101. **`PerforatedReceiptTicketView` (101)**: Hand-drawn session completion ticket with semicircular side punch cutouts, dashed perforation tear line, `SpaceMono` telemetry table, and `HmacSHA256` digest.
102. **`CadenceBarcodeCanvasView` (102)**: Deterministic 36-bar vertical cadence barcode drawn on the bottom stub of every session receipt.
103. **`StampSealEmblemCanvasView` (103)**: Geometric vector stamp seal renderer (`Solar Horizon`, `Weimar Grid`, `Chrono Prism`, `Studio Ledger`) in `nothing_red` / `colorPrimaryContainer` / `colorPrimary`.
104. **`StampPlungerCommitView` (104)**: Spring-loaded physical stamp press bar (`Curves.A`, `260ms`, `10ms` clunk) to bank a completed session receipt.
105. **`SystemGuardLayerRowView` (105)**: Full-bleed row for each of the 5 OS protection layers (`01 Accessibility Shutter`, `02 UsageStats Meter`, `03 Held Pings Catcher`, `04 Overlay Shield`, `05 Monotonic Clock & HMAC`) with armed indicator and deep-link trigger.
106. **`FourCellChargeMatrixView` (106)**: 4-socket hardware readiness gauge (`25% / 50% / 75% / 100% ARMED`) in the System Guard header.
107. **`HeldNotificationRowView` (107)**: Custom Canvas row inside the post-session digest showing app glyph, sender, timestamp in `SpaceMono`, message body (redacted if session is active), and `[✕]` dismiss tile.
108. **`CravingLeaderboardBandView` (108)**: Full-bleed breakdown in `HORIZON` ranking the top intercepted apps and peak impulse hours with proportional deflection bars.

---

## 4. Technical Architecture & Staged Build Sequence

### 4.1 System & Custom-Control Rendering Architecture

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                     RESOURCE & FEEL FOUNDATION (§1–§7)                       │
│  • res/values/colors.xml & res/values-night/colors.xml (19 Zero-Green Slots) │
│  • res/values/dimens.xml (12dp/24dp radii, 12dp gap, 80dp nav, 148dp/96dp)   │
│  • res/font/ (PlusJakartaSans = words, SpaceMono = numbers, Stencil = spice) │
│  • MonolithCurves (Curves.A, Curves.B, Curves.C + {180,260,280,420,520}ms)   │
│  • MonolithFeelEngine (1/4/8/10ms OneShot Haptics + 4-Partial Float-PCM)     │
└──────────────────────────────────────┬───────────────────────────────────────┘
                                       ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│              108 HAND-DRAWN CUSTOM CANVAS & 3D SHADER CONTROLS               │
│  • Zero allocations in onDraw (preallocated Paint, RectF, Path, Strings)     │
│  • Zero stock Material widgets on main screens                               │
├──────────────────────────────────────┬───────────────────────────────────────┤
│   IDLE FOCUS DECK (Light Tokens)     │  SEALED VAULT CHAMBER (Night Tokens)  │
│  • ThreeJsMachinedRingView (015)     │  • LockedAnisotropicChronometer (034) │
│  • RotaryWheelCanvasView (016)       │  • GraceWindowCountdownBarView (035)  │
│  • HeroArchitecturalTimerView (017)  │  • ActiveTaskExecutionConsole (037)   │
│  • HapticPresetChipView x5 (019)     │  • MechanicalLatchCheckboxView (038)  │
│  • RombiKnobView (020)               │  • SealedShieldTelemetryStrip (044)   │
│  • HorizontalVolumetricRibbon (021)  │  • WeeklyBreakTokenBadgeView (045)    │
│  • ActiveIntentionBandView (023)     │  • Hold60sPressureValveView (046)     │
│  • SlideToStartView (024)            │  • CipherPhrasePlateView (047)        │
├──────────────────┬───────────────────┴──────────────────┬────────────────────┤
│  SHIELD MATRIX   │           TASK SEQUENCER             │  HORIZON & CURIO   │
│  • BagTiles (053)│  • TotalWorkloadHorizonView (067)    │  • DayFaders (080) │
│  • 3-Stop Sled   │  • QuickAddTaskModuleView (068)      │  • TowerView (081) │
│    with One-Way  │  • ProportionalTaskBandView (069)    │  • StackedChart    │
│    Ratchet (057) │  • PriorityOrderKeyPairView (070)    │  • ThumbDrum (083) │
│  • 24h Routine   │  • CueToTimerKeyView (071)           │  • 6 Kinetic Toys  │
│    Timeline (060)│  • TaskSubStepDrawerView (072)       │  • FallingSquares  │
└──────────────────┴──────────────────────────────────────┴────────────────────┘
```

### 4.2 Staged Implementation Order (Following `DEVELOPER-GUIDE.pdf` §10)
1. **Stage 1 — Tokens, Dimens, Fonts & Strings First (§1, §2, §3, §8)**:
   - Create `res/values/colors.xml` and `res/values-night/colors.xml` with the exact 19 Zero-Green tokens; `res/values/dimens.xml` with all named geometry metrics (`12dp`, `24dp`, `80dp`, `148dp`, `96dp`, `48dp`, `14dp`, `4dp`); wire the 3 font families in `res/font/`; update `res/values/strings.xml` with concrete ritual-flavored copy.
2. **Stage 2 — The Feel Layer: Curves (§5), Haptics (§6) & Float-PCM Synth (§7)**:
   - Implement `MonolithCurves` (`A`, `B`, `C` + `{180, 260, 280, 420, 520}ms`), the `1/4/8/10ms` `VibrationEffect.createOneShot` helper, and rewrite `TactileSoundEngine` as the pure zero-file Float-PCM synthesizer (`partial weights { 0.6, 0.28, 0.37, 2.2 }`, `exp(-0.0114)` body decay, `exp(-0.0313)` tail decay, `AudioFocusRequest`), removing the ambient sound mixer completely.
3. **Stage 3 — Core Custom Control Library & `HapticPreset` Archetype (§4)**:
   - Build the foundational zero-allocation custom Canvas control primitives (`HapticPresetChipView`, `SledSwitchView`, `AdvanceLeverView`, `RombiKnobView`, `TileNumberView`, `MechanicalLatchCheckboxView`, `InlineTextEditorWellView`, `BottomHardwareNavView`, `MonolithPullDownBarView`).
4. **Stage 4 — Hero Control (`ThreeJsMachinedRingView` + `RotaryWheelCanvasView`), Idle Deck & Dedicated Sealed Vault Chamber**:
   - Build the **3D Three.js Precision Machined Ring Dial** with anisotropic metal GLSL shaders + `148dp/96dp` `HeroArchitecturalTimerView` + `HorizontalVolumetricRibbonView` + `SlideToStartView`, and build the dedicated **Sealed Vault Chamber** screen (`SealedVaultChamberView`, `ActiveTaskExecutionConsoleView`, `Hold60sPressureValveView`, `WeeklyBreakTokenBadgeView`) enforcing the single unbreakable lock + 1/week 60-second emergency break.
5. **Stage 5 — BagTiles, One-Way Ratchet Shield, Proportional Task Queue, TowerView & Curio Vault**:
   - Build the Shield controls (`BagTilesGridView`, `ThreeStopSledSwitchView` with One-Way Ratchet lock, `Routine24hTimelineView`, `DayOfWeekSevenCellView`), Task controls (`TotalWorkloadHorizonView`, `ProportionalTaskBandView`, `TaskSubStepDrawerView`), Horizon & Curio controls (`SevenDayFaderStripView`, `TowerAccumulationView`, `StackedHorizonChartView`, `HorizontalThumbDrumView`, `FallingSquaresCelebrationView`, 6 Kinetic Artifact canvases), and Overlays (`AirlockBreathingChamberView`, `LandscapeNightstandClockView`, `PerforatedReceiptTicketView`).
6. **Stage 6 — Verification Against the §11 Acceptance Checklist**:
   - Run `compile_applet` and `gradle :app:testDebugUnitTest` to verify every control, token, zero-green invariant, and user journey.

---

## 5. Measurable Acceptance Checklist (§11 — Every Line Verified)

- [ ] **108 Hand-Drawn Custom Controls / Zero Stock Widgets**: Every control on main screens is custom-drawn on `Canvas` (plus the 3D Three.js WebGL ring dial) with pre-allocated `final Paint`, `RectF`, and `Path` fields; zero stock Material widgets (`Button`, `Switch`, `Slider`, `Card`, `Checkbox`, `BottomNavigationView`) appear on designed screens.
- [ ] **19 Resource Tokens Only**: Every color in the app resolves to one of the 19 tokens in `res/values/colors.xml` and `res/values-night/colors.xml`; every token is strictly Zero-Green (`G < max(R, B)`).
- [ ] **One Saturated Accent**: `colorPrimaryContainer` (`#E8451E` / `#F46036`) is the only saturated accent and appears only where it means state (session timeline/ring arc, active sled/power tiles, armed commit bar).
- [ ] **Numbers Are Architecture (`148dp` Hero Digits)**: Every number on every screen renders in the mono font (`SpaceMono`/`JetBrainsMono`); mixed strings split words (sans) and quantities (mono); hero countdown digits render at `148dp` on a `96dp` column width.
- [ ] **Two Corner Radii & Full-Bleed Sections**: Only `12dp` and `24dp` corner radii exist app-wide; screens are full-bleed edge-to-edge stacked sections separated by `1dp` `colorOutlineVariant` hairlines with an `80dp` custom bottom navigation bar.
- [ ] **Three Motion Curves & 5 Durations**: Every animation uses `Curves.A`, `Curves.B`, or `Curves.C` (plus `OvershootInterpolator` for spring-back levers) and a duration from `{180, 260, 280, 420, 520}ms`.
- [ ] **Clicks, Not Buzzes (`1/4/8/10ms`)**: Every haptic is a `VibrationEffect.createOneShot(1/4/8/10ms, DEFAULT_AMPLITUDE)`; `performHapticFeedback` appears nowhere.
- [ ] **Pure Float-PCM Synth & Zero Ambient Mixer**: No audio files in the APK and no ambient sound mixer; all UI sound comes from the dedicated Float-PCM synth thread using partial weights `{ 0.6, 0.28, 0.37, 2.2 }` and dual exponential decays `exp(-0.0114)` / `exp(-0.0313)`.
- [ ] **Dedicated Sealed Vault Chamber & Ruthless Enforcement**: Starting a session transitions into the dedicated **Sealed Vault Chamber** screen; no lock-tier selector exists; after the 10s grace window, early stopping requires spending the **1/1 Weekly Emergency Break Token** via a **60-second continuous hold** + confirmation phrase; the Shield tab enforces **One-Way Ratchet Mode** so rules can be tightened mid-session but never loosened.
