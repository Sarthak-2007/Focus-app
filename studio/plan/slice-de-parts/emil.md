# Emil — slice D/E part: THE IMPRINT SLOT, THE STRIKE, THE PRESSURE LIP

*Seat: Emil, Wild, print (06). Coordinator: Marlowe. Law as given:
BEYOND-deck W4 in its LIVE WIRE form (Ines — synchronous verdicts
≤5ms at DOWN and at the 0.68 gate; Park / 1500ms deadline / UNKNOWN /
Canary / 20s poll are dead) · F-TENANCY (the slot is Emil's, the
sentences are Suki's, failure at 15sp, counts at plate) · REGISTER
(t=0, spectacle = exactly 4 freezes, type ladder 104/72/50/22/15/10).
Line numbers verified this run against the live tree `Focus-app/`,
not the token/counterfoil build copies.*

## (1) Verdict

**BUILDS AS AMENDED.** The slot, the tenancy, the Strike and the lip
all survive contact with the real code — but the filed text cannot
be built literally, for reasons the code, not taste, supplies:
there is **no slot under the slide today at all** (the count lives
in the instrument panel's header, in the wrong words, counting the
wrong tiers), there is **no sealed-count storage anywhere** (the
freeze the law demands has no home), and the filed phrase "at the
seat frame" names **two different frames** that Vega's and Opal's
joints have since pried 210ms apart. Eight amendments, all exact,
in §3: A1 dual-size box · A2 Strike fires on t=0, not onLatch ·
A3 count grammar/source (PEEK in, singulars, numeral-first) ·
A4 frozen snapshot persisted on the session row · A5 ink-settle as
alpha lerp · A6 mid-slide latch mechanism · A7 UNKNOWN tenant dead,
pending = held silence · A8 the slot is built new under the pinned
slide and the header tenant is evicted. Spectacle: **the Strike
claims 0 freezes** — it is a print event that starts on a frame
that already exists; the ledger stays at four.

## (2) The joint against the real code (today)

`TactileDeckScreen.kt` (534 lines) and its suppliers, as shipped:

- **The slide host is exactly where the brief said — and it is
  empty underneath.** The pinned commit bar is the `Box` at
  `TactileDeckScreen.kt:481–532` (12dp horizontal / 8dp vertical
  padding): idle → `SlideToStartView` at `:487–505` (68dp,
  `testTag("slide_to_start_view")`); running → the grace-abort or
  emergency-valve `BlockitActionChip` at `:506–531`. **Nothing
  prints beneath it, in either branch.** There is no double rule,
  no slot, no lip, no reserved space. Worse for any naive build:
  the slide *unmounts* when a session starts (`if (!isSessionRunning)`
  at `:486`), so a slot placed inside that `Box` would die at the
  exact frame the SEALED tenant is born. The slot must be a sibling
  **below** the `Box`, always composed (§3, A8).
- **The only count tenant in the app lives in the wrong home, in
  the wrong grammar, counting the wrong set.** Inside the
  instrument panel, header `Row` at `:196–225`: the right-hand
  `Text` at `:218–225` prints
  `if (isSessionRunning) "SHUT $blockedCount APPS" else "WILL SHUT $blockedCount APPS"`
  — Anonymous Pro, 10sp, letterSpacing 2.sp, `onSurfaceVariant`,
  with a hand-rolled letterpress shadow. Against filed law it is
  wrong four ways: word order (filed `{n} APPS WILL SHUT`, numeral
  first), sealed verb (filed `{n} APPS HELD`; `SHUT` is past-tense
  and false of the PEEK tier), no singular (`1 APPS` prints today),
  and the count itself —
- **`blockedCount` counts LOCKED only.** `:120`:
  `remember(apps) { apps.count { it.resolvedTier == AppBlockTier.LOCKED } }`.
  Filed law counts LOCKED **plus** PEEK (`AppBlockTier.PEEK_2M`,
  `MonolithModels.kt` — the tier enum at the head of that file).
  Today's sealed line therefore undercounts every loadout that
  carries a peek app, and it is *live* (`remember(apps)` re-derives
  on any `apps` emission) — the opposite of the filed freeze.
- **The session-state text beside it is the hero's eyebrow, not a
  tenant.** `:200–216`: `TOTAL` / `SESSION RUNNING` /
  `BREAK · {n} MIN`. It stays where it is; the imprint does not
  touch it. (For the record: `MainActivity.kt`'s header pills —
  `HELD {nn}` notifications, `GUARD {n}/4` in
  `MonolithCustomComponents.kt` `MonolithPullDownHeader` — are
  chrome facts about other things; none is this slot's tenant and
  none moves.)
- **The slide's badge is not a tenant and the slot never reads
  it.** `subMonoBadge` is set at `TactileDeckScreen.kt:498`
  (`"${dialMinutes}M"` / `"FLOW"` — per-block minutes, Aldo's
  joint) and drawn inside the view's canvas at
  `ui/views/BlockitCoreViews.kt:1124`. Print in the track is the
  slide joint's; the imprint's only inputs are tenancy inputs (§3).
- **No shared plate composable exists.** `ui/components/
  MonolithCustomComponents.kt` (448 lines) holds
  `rememberMixedTypographyText`, `FullBleedSectionHeader`,
  `BlockitActionChip`, `MonolithPullDownHeader`,
  `UnifiedStatusGuardDrawer`, `inkControlFill`/`onInkControlFill`
  — no imprint, no plate line, no double rule (its only divider is
  the header's single `HorizontalDivider` at `:313`). The plate
  *style* does exist, exactly as filed: `PillBadgeTextStyle`,
  `ui/theme/Type.kt:85–92` — Anonymous Pro Bold, 10sp, lh 12,
  letterSpacing 2.0, `includeFontPadding = false`. The Deck's count
  `Text` ignores it and hand-rolls the same numbers inline
  (`:220–222`) plus the shadow hack — the drift the new composable
  ends by construction.
- **Tenancy drivers that exist:** session —
  `MonolithViewModel.activeSession: StateFlow<FocusSessionEntity?>`
  (`MonolithViewModel.kt:266`), derived on the Deck as
  `isSessionRunning = activeSession != null && activeSession.isActive`
  (`TactileDeckScreen.kt:118`). Apps — `viewModel.apps`
  (`MonolithViewModel.kt:247`, from `repository.allAppsFlow`).
  Readiness — `viewModel.systemGuardStatus: StateFlow<SystemGuardStatus>`
  (`MonolithViewModel.kt:241`; `SystemGuardStatus` at
  `MonolithRepository.kt:29–50`, `readinessCount` = accessibility +
  usage + notification + device-admin at `:41–48`), refreshed only
  via `refreshSystemGuard()` (`MonolithViewModel.kt:1061`) and
  MainActivity's ON_RESUME observer. Sites — `viewModel.blockedSites`
  (not used by this joint; see A3).
- **Tenancy drivers that do NOT exist:** (a) **The Deck receives
  no readiness at all** — `MainActivity.kt:281` calls
  `TactileDeckScreen(...)` with no guard/readiness parameter,
  though it already collects `systemGuardStatus` at `:179`.
  TRUTH tenancy is unwirable until that parameter lands. (b)
  **No primed state exists anywhere** — `grep prime` over the whole
  tree: zero hits. Routines exist (`viewModel.routines`,
  `MonolithViewModel.kt:103`; `RoutineEntity.loadoutName` is a bare
  String in `MonolithModels.kt`), but W1's prime function is
  unbuilt and nothing stages a routine loadout for the Deck to
  count. (c) **No PROVING state** — `MonolithRepository.startSession`
  (`:869–918`) inserts `isActive = true` from birth (`:910`); there
  is no latch callback distinct from commit
  (`SlideToStartView.onSlideCommitted`, `BlockitCoreViews.kt:1185`,
  release-only at the superseded 0.82 today — Vega's joint rebuilds
  it). Opal's PROVING → SEALED-RUNNING transition is a build, not a
  field. (d) **No hash, no list-proof, no Shield-running field** —
  Suki's classes F (hash mismatch) and B (shield down) have no
  measured source in this tree; the Live Wire registries / Otis
  heartbeat store is that source when it lands (Rosa's joint).
- **Session end is observable on the Deck.** Every exit funnels to
  `isActive = false`: `repository.disarmActiveSession`
  (`MonolithRepository.kt:1085`) via the valve/grace paths, or the
  ViewModel ticker flipping a completed session
  (`MonolithViewModel.kt:327–335`). The Deck sees it as
  `isSessionRunning` true → false. That flip is the lip fade's
  trigger and the SEALED → IDLE swap's frame (§3).
- **Motion vocabulary is closed.** `MonolithCurves.kt` offers
  durations {180, 260, 280, 420, 520} and easings A/B/C only. The
  Strike's 110ms / `CubicBezierEasing(0.9, 0, 1, 0.4)`, the lip's
  240ms fade and the 400ms settle are **not in the file** — they
  enter as named constants (§4), they are not improvised inline.
- **Tones check out.** Lip night `#4A423C` is a locked token:
  `NightColorSurfaceSheet`, `ui/theme/Color.kt:34`. Lip day
  "ground-lightened at 50%" is `TokenColorSurfaceBright #FAF6F0`,
  `Color.kt:15`, at 50% — the same token D3 spends as the hero's
  catch ghost. No new hex is proposed anywhere in this part.

## (3) Amended spec — the slot, the Strike, the lip, buildable

### 3.0 Geometry — one fixed assembly under the pinned slide (A8, A1)

New composable `ImprintSlot` in
`ui/components/MonolithCustomComponents.kt`, composed in
`TactileDeckScreen` as a sibling **immediately below** the pinned
`Box` (`:481–532`), inside the outer `Column` — therefore composed
in idle, primed, mid-slide, sealed, grace and valve states alike,
and immune to the slide's unmount at `:486`. Horizontal padding
12dp, matching the slide `Box`, so slot width = slide width.

Fixed stack, top to bottom, **identical in every tenancy** (this
fixity *is* the HELD-never-CLOSED law, Δy = 0, implemented):

| Band | Height | Content |
|---|---|---|
| Double rule | 1dp line + 2dp gap + 0.5dp line = 3.5dp | `outlineVariant` at filed alphas (1dp @ 22% ink over 0.5dp @ 12% — my masthead rule, promoted to the slot's head) |
| Gap | 6dp | — |
| Text box | **20dp, fixed** | One `Text`, baseline-pinned (below) |
| Lip zone | **4dp, always reserved** | Lip drawn only while sealed; empty otherwise — the lip's arrival moves nothing |
| Bottom inset | 6dp | — |

**Total added pinned height: 39.5dp, once.** It is paid from the
scroll viewport (the `weight(1f)` column at `:174–181` shrinks),
never from a tenancy change. The text box is 20dp because the box
must fit the largest tenant (TRUTH at 15sp lh 20, §3.1) — counts at
plate sit in the same 20dp box on the **same baseline**: both
styles render with `includeFontPadding = false`
(`PillBadgeTextStyle` already does), the `Text` is bottom-aligned
in the box, and the plate style's lineHeight is set to 20sp inside
this composable only (glyph size stays 10sp; the extra leading
sits above the glyphs, so the baselines coincide — verified on the
first render; a ≤1dp residual is a render bug to fix in the
composable, never by moving the box). Nothing below the slot
exists on the Deck; the band nav in `MainActivity` sits below a
fixed-height sibling and cannot be pushed by any tenant, ever.

### 3.1 Tenancy — drivers, order, strings

One `when`, evaluated in this order (sealed first — SEALED evicts
even TRUTH; TRUTH evicts every *unsealed* tenant):

```
1. SEALED   isSessionRunning == true
            → "{n} APPS HELD"   (n = frozen snapshot, §3.2)
2. TRUTH    unsealed && readiness.firstFailure != null
            → Suki's sentence for that class, verbatim (§3.1b)
3. IDLE /   unsealed && readiness healthy
   PRIMED   → "{n} APPS WILL SHUT" (n per §3.1a)
4. PENDING  unsealed && readiness never yet read in this residency
            → "" (empty text box, space held — A7)
```

**3.1a Count tenants (plate).** Anonymous Pro **700**, **10sp**,
tracking **+2.0**, via `PillBadgeTextStyle` (lineHeight overridden
to 20sp per §3.0), tier: night `#9C8E84` secondary / day
`onSurfaceVariant` — the tones the header tenant already uses, so
the relocation changes no voice. Strings, exact:

- Idle: `"{n} APPS WILL SHUT"`; singular `"1 APP WILL SHUT"`.
- Primed: **the identical string, character for character**,
  counted against the routine's staged loadout. No time, no name,
  no countdown — nothing about the routine prints here, ever.
  Driver: until W1's prime function lands, primed is
  indistinguishable from idle *by design* (same string, same
  pipeline); when it lands, the only change is the input set —
  the prime function's staged `List<BlockedAppEntity>` replaces
  `apps` in the count below. This part does not invent a prime
  field; it names the seam.
- Count source (idle, and primed once staged):
  `apps.count { it.resolvedTier == LOCKED || it.resolvedTier == PEEK_2M }`
  — **PEEK in** (A3). This deliberately disagrees with
  `MainActivity.kt`'s LOCKED-only `blockedAppsCount` feeding the
  nav badge: HELD/WILL SHUT is a Deck-only fact about what the
  seal stops, and a peek app *is* stopped, on a lease. Uma's APPS
  joint owns the cross-surface ruling; until she rules, the
  divergence is documented here so no QC pass "fixes" it.
- Sites: the filed extended form (`· {s} SITES …`) is **not**
  built in this slice. The task's filed law for this joint is
  apps-only, `blockedSites` has no tier semantics to freeze
  against, and a second numeral in the slot is a second fact.
  Recorded as a cut, not an oversight — my part file's sites
  segment waits for its own merge.

**3.1b TRUTH tenants (Suki's sentences, her size).** Atkinson
Hyperlegible **700**, **15sp**, tracking **+0.5**, lh 20, tier one
step stronger than healthy (night `#D2C6BB` primary / day ink
tier) — per W4/F-TENANCY: *"plate = furniture, body = truth."*
Sentences verbatim from Suki's table (`beyond-deck-parts/suki.md`
§3.2): Class A `BLOCKING IS OFF — TAP TO FIX` · B `BLOCKING
STOPPED — TAP TO RESTART` · C `USAGE ACCESS IS OFF — TAP TO FIX` ·
D `OVERLAY IS OFF — TAP TO FIX` · E `BLOCK LIST IS EMPTY — TAP TO
ADD` · F `BLOCK LIST OUT OF SYNC — TAP TO FIX`. **Class G
(`CAN'T CHECK BLOCKING — TAP TO RETRY`) does not ship** (A7):
under Live Wire there is no UNKNOWN — a service instance absent
from the registry is positive evidence and tenants as its OFF
class; a store never yet read tenants as PENDING silence (§3.1),
never as a healthy count (Suki's own first-paint law) and never
as G. Driver: the single readiness store (Rosa/Otis joint —
Live Wire verdict + heartbeat) exposed to the Deck as a new
`TactileDeckScreen` parameter; in today's fields it maps to
`systemGuardStatus` (A/C/D), `apps` held-count = 0 (E), and —
until the wire lands — B and F have no source and simply cannot
tenant, which is stated here rather than faked. First failure
only, Suki's priority order (service → usage → overlay → list).
The TRUTH tenant carries the repair tap: `ImprintSlot` takes
`onTruthTap: (() -> Unit)? = null`; Suki's geography joint binds
route + 48dp hit area to it. In every other tenancy the slot is
pure print, not a button (my furniture law, kept).

**3.1c Transitions.** IDLE ↔ TRUTH ↔ PRIMED (unsealed, re-measure
events only): slug swap — old drops 3dp + fades 60ms, new drops
in 90ms, `CubicBezierEasing(0.55, 0, 1, 0.6)`, 150ms total,
transform-only inside the fixed box (layout Δy still 0).
**Exception — hand-back after repair** (W4 governs over my part
file): measured re-read + 500ms stability beat, then a **90ms
cross-fade** straight to the healthy tenant; no restored line,
ever; total ≤890ms. Tenant changes are silent (no haptic, no
sound) except Suki's hand-back tick, which her joint owns.
SEALED → IDLE at session end: slug swap, fired on the end flip
(§3.3). Any tenant → SEALED: **never a swap — the Strike (§3.3).**

**3.1d No change mid-slide — the mechanism the filed text lacked
(A6).** "No change in any condition" is not free: `apps` or the
readiness store could emit during a 1.2s drag. So: the slide view
gains a DOWN callback (Vega's joint already rebuilds DOWN for the
Live Wire verdict and hit-test — the hook rides his work:
`onSlideInteractionStart`, fired on the same DOWN that reads the
verdict). On that callback the Deck latches the currently
displayed tenant (string + style) into
`imprintLatched: ImprintTenant?`; while the slide phase is
DRAG / MESH / TAKEN (Vega's `SlidePhase`), `ImprintSlot` renders
the latch and ignores its live inputs. The latch releases on
exactly two frames: SEATED→t=0 (replaced by the Strike) or return
to REST (refusal / spring-back — tenancy re-reads live; the
refusal itself is Rosa's interlock + Suki's already-standing
TRUTH tenant, which was on screen before the thumb moved — no
new line appears at a refusal, ever).

### 3.2 The frozen sealed count — capture and storage (A4)

- **What:** `n_sealed = count(LOCKED) + count(PEEK_2M)`, evaluated
  **once**, from `dao.getAllAppsSnapshot()` — the same snapshot
  discipline the cascade proves against, never the UI Flow (the
  Flow's `apps` and the DAO snapshot can differ by an emission;
  the sealed line prints the proved set, not the displayed set).
- **When:** inside `MonolithRepository.startSession`
  (`:869–918`), in the same call that inserts the session — the
  count is a fact of the seal act, written by the act, or it is a
  ghost (the token spec's transaction law, applied to print).
- **Where stored:** new column on `FocusSessionEntity` —
  `heldAppsAtSeal: Int = -1` (`MonolithModels.kt`, beside
  `totalStudyBlocks`) — written at insert; the Deck's SEALED
  tenant reads `activeSession.heldAppsAtSeal` and nothing else
  for the session's whole life. A peek lease, a tier edit on
  Shield, a mid-session revocation: none reprints it (HOLD; the
  gap is told at unlock / on the receipt, per Suki). `-1`
  (pre-slice rows) renders the tenant from the live count once
  and freezes that — unreachable once startSession writes it,
  stated so the fallback is a decision, not a crash.
- **Migration:** this tree is still DB v7 with
  `fallbackToDestructiveMigration(true)` standing
  (`MonolithDatabase.kt:21,47`), so the column compiles today;
  in the plan of record it **rides REGISTER's 9→10 plumbing
  batch** as one ADD COLUMN with default −1 (append-spirit,
  beside Rurik's/Otis's tables) — it may not ride 7→8 (TOKEN-SPEC
  alone, CLOSED) nor 8→9 (routines batch, enumerated). Voss
  registers it; flagged in §7 as the fork if he refuses the ride.

### 3.3 The Strike — frame, animation, budget

**The frame (A2 — the disambiguation this joint exists to make).**
Two frames are in play and the filed text conflated them:

- **onLatch** — Vega's powered seat completes: displayed progress
  reaches 1.00 under Cato's spring (400/16, 110ms) after the 0.84
  catch crossing; latch knock at d ≥ 0.97. This is the gesture's
  visual climax. It orders; it proves nothing.
- **t=0** — Opal's PROOF-SEAL FRAME, REGISTER's definition:
  `t0 = max(last link CONFIRMED-SHUT, latch + 210ms, pawl-seated frame)`,
  the first vsync the controller executes PROVING → SEALED-RUNNING.

**The Strike fires on t=0, not onLatch.** Print's own law decides
it: a strike on the latch would print `HELD` up to 210ms before
the seal is proved — celebration before the fact, in ink, the
exact charge my part file (§2.2) levels at threshold strikes.
"W4 says *seat frame*" is honoured literally: t=0's third term
*is* a seat frame — the pawl's, the proof's seat, as opposed to
the sled's. On the Deck the trigger is concrete: the first frame
`isSessionRunning` is observed true for a session id this Deck
residency has not struck (`struckSessionId` state, keyed by
`activeSession.id`). Corollaries, all normative:

- **Settled on return:** if the Deck first composes already
  sealed (tab return, process restore mid-session), the SEALED
  tenant + lip render **settled** — scale 1.0, ink settled, lip
  at full alpha, **no Strike**. The Strike is struck once, at
  the sealing, or not at all; it is never replayed on view
  (the Edition's law, applied at furniture scale).
- **No proof, no Strike:** a refused arm never reaches t=0;
  the latched tenant (§3.1d) releases back to live tenancy.
- **Budget: 0 freezes, 0ms serial.** The Strike is two
  `Animatable`s launched *on* the t=0 frame, concurrent with
  Orson's cadence and Wren's shot; it holds no frame, delays
  no transition, and gate-to-session is untouched. It enters
  S2's audit as the existing row "W4 the Strike / pressure
  lip — 0" — unchanged.

**The animation, exact.** On the trigger frame, the SEALED
`Text` composes under `Modifier.graphicsLayer`:

- Scale: `Animatable(1.04f)` → `animateTo(1.0f, tween(110,
  StrikeEasing))`, `StrikeEasing = CubicBezierEasing(0.9f, 0f,
  1f, 0.4f)`; `transformOrigin = TransformOrigin(0f, 0.5f)` —
  struck from the left margin, like type set against it. No
  overshoot (iron does not bounce).
- Ink-settle: `Animatable(0.82f)` → `animateTo(0.88f,
  tween(400, MonolithCurves.EasingA))`, applied as the text
  colour's alpha multiplier (A5: glyph-centre density is not
  addressable on a Compose `Text`; at 10sp the alpha lerp over
  the ground is its faithful render — if the device trial
  reads it as nothing, the settle dies and the scale alone
  carries the Strike; the facts never depended on it).
- The old header tenant's letterpress shadow does **not**
  follow the slot: `PillBadgeTextStyle` carries no shadow and
  the slot adds none. One press treatment (the settle), not two.
- Reduced motion (system animator duration scale = 0): the
  Strike is a single-frame step — tenant + lip at final values
  on the t=0 frame. Facts identical; theatre yields.
- Haptics/sound: none of its own. It is timed to the seat
  knock and Orson's seal and borrows neither.

### 3.4 The pressure lip — draw, life, fade

- **Draw:** inside `ImprintSlot`, in the always-reserved 4dp lip
  zone: a `Canvas(Modifier.fillMaxWidth().height(1.dp))` drawing
  one line, `drawLine(color, Offset(0f, size.height / 2),
  Offset(size.width, size.height / 2), strokeWidth = 1f)` —
  strokeWidth in DrawScope is physical px, so the lip is
  **exactly 1px** on any density, spanning the slot's full
  width (margin to margin). Placement: text box bottom + 2dp
  spacer + this 1dp canvas puts the line at **baseline +3dp**
  within the trimmed (`includeFontPadding = false`) metrics —
  layout-exact as specced; the builder verifies against the
  rendered baseline once, in the composable, never by moving
  the box.
- **Tone:** day `TokenColorSurfaceBright #FAF6F0` at **50%**;
  night `#4A423C` (`NightColorSurfaceSheet`) at **35%** over
  ground `#171412` — selected by the Deck's existing
  `effectiveNight` (`TactileDeckScreen.kt:119`). Both are locked
  tokens at stated opacities (H-7 clean).
- **Life:** alpha 1 for exactly the seal's life — present from
  the t=0 frame (struck in with the tenant; on settled return,
  present at full alpha with no Strike) until the end flip.
  It is a state rendering, not a record: no per-session notch,
  no accumulation, anywhere (my part file's refusal stands,
  whole, for the Deck).
- **Fade (session end):** the trigger is the Deck observing
  `isSessionRunning` true → false (§2 — disarm, grace cancel,
  or ticker completion). On that frame: lip `Animatable(1f)` →
  `animateTo(0f, tween(240))`, then the lip uncomposes (the 4dp
  zone remains — reserved, empty, Δy = 0); on the same frame
  the SEALED tenant slug-swaps to the re-measured IDLE tenant
  (§3.1c), so the Deck is truthfully idle before any receipt
  strikes over it. If the Deck leaves composition mid-fade
  (tab switch inside 240ms), the fade dies with it — lawful:
  the lip renders a state, and the state is over either way.

## (4) Code sites

| Site | Change |
|---|---|
| `ui/components/MonolithCustomComponents.kt` | **New:** `ImprintSlot(tenant, onTruthTap)` + private `DoubleRule()` + lip `Canvas` + Strike animatables (~§5) |
| `ui/screens/TactileDeckScreen.kt:218–225` | **Delete** the header count `Text`; header `Row` keeps the eyebrow only |
| `ui/screens/TactileDeckScreen.kt:481–532` | Compose `ImprintSlot` as sibling below the pinned `Box`; new params `systemGuardStatus` (or the readiness store), latch state (§3.1d), `struckSessionId` state |
| `MainActivity.kt:281` | Pass readiness + (already-collected at `:166,179`) into `TactileDeckScreen` |
| `data/MonolithModels.kt` (`FocusSessionEntity`) | `heldAppsAtSeal: Int = -1` |
| `data/MonolithRepository.kt:869` (`startSession`) | Snapshot count from `dao.getAllAppsSnapshot()` (LOCKED + PEEK_2M), write into the inserted session |
| `ui/views/BlockitCoreViews.kt` (`SlideToStartView`) | `onSlideInteractionStart` callback on DOWN — **only if Vega's rebuild hasn't already landed an equivalent hook**; otherwise host-side only |
| `ui/theme/MonolithCurves.kt` | `StrikeEasing` + `DURATION_110 / 240 / 400` constants |
| `data/MonolithDatabase.kt` | No change in this tree (v7 + destructive fallback); REGISTER 9→10 gains the ADD COLUMN line (Voss) |

No change: the eyebrow (`:200–216`), the badge (`:498`),
`SlideToStartView`'s drawing, the receipt, chrome.

## (5) Price in lines

| Item | Lines |
|---|---|
| `ImprintSlot` composable (tenancy `when`, both styles, transitions) | +95 |
| Double rule + lip Canvas + Strike/settle/fade animatables | +70 |
| `TactileDeckScreen`: delete header tenant (−8), slot wiring, latch + strike-key state | +62 / −8 |
| `MainActivity`: pass readiness through | +3 |
| `FocusSessionEntity.heldAppsAtSeal` + `startSession` snapshot | +9 |
| `SlideToStartView.onSlideInteractionStart` (if not Vega's) | +6 |
| `MonolithCurves`: StrikeEasing + 3 durations | +5 |
| **Net** | **≈ +242 / −8 (≈ +234 net), ~0.75 build day incl. render checks** |

The in-memory fallback for §7's fork (snapshot in a ViewModel
`StateFlow` instead of the column) prices at −9 / +14 and is
**not** the specced build — see the fork.

## (6) Risks

1. **Baseline coincidence across two font sizes in one box**
   (10sp Anonymous vs 15sp Atkinson, §3.0). Layout Δy is 0 by
   construction (fixed box); a ≤1dp *glyph* baseline wobble
   between tenants is the residual. Mitigation is one render
   check; if it wobbles, pad the plate style inside the
   composable — never resize the box per tenant (that would
   resurrect CLOSED by the back door).
2. **The snapshot and the last idle print can differ by one.**
   Idle counts the Flow; sealed counts the DAO snapshot at the
   seal. A tier edit on Shield between Deck entry and the slide
   makes both prints true at their moments and different from
   each other. Named here so it is never "fixed" by making the
   sealed count live — live is the one thing it must not be.
3. **TRUTH freshness is the heartbeat's, not the slot's.**
   Today's `systemGuardStatus` refreshes on ON_RESUME + manual
   refresh only; a permission dying while the Deck is foreground
   tenants late. The slot reads the store and never polls (the
   20s poll is dead and stays dead); cadence is Rosa/Otis's
   joint. If their store slips, my tenant is stale, not wrong
   in kind — flagged as a dependency, not absorbed as a poll.
4. **Strike replay is the classic bug of this pattern.**
   Keying on session id + observed transition (§3.3) is the
   whole defence; the acceptance test is named: arm, seal,
   leave the Deck, return mid-session — no Strike, lip present,
   tenant settled. Rotate/recompose likewise.
5. **Room.** +39.5dp pinned is real money under Kato's trial
   (REGISTER: fixed sum 796, reserve = H − 796, return under
   H = 820). The slot is outside the 796 scroll stack but
   shrinks what of it is visible. First compression if the
   room fails: rule+gap 9.5dp → 5dp and bottom inset 6 → 4
   (−6.5dp) — the text box and lip zone never compress.
   Rosa's reserved 22dp refusal slot, if built in the same
   band, stacks *above* the double rule (slide → Rosa slot →
   rule → imprint): +30dp more, hers to price, adjacency
   named here so the merge sees the whole band at once.
6. **Ink-settle may read as nothing at 10sp** (A5). It is
   priced inside the composable and dies by its own gate
   without touching the Strike's scale or any fact.

## (7) Fork for Ren

**One fork: where the frozen sealed count lives.**
(a) **Persisted on the session row** (specced, §3.2) — survives
process death, receipt and Deck read one number, rides
REGISTER's 9→10 batch as one ADD COLUMN; needs Voss to register
the ride. (b) **In-memory in the ViewModel** — no migration, but
a process death mid-session forces a restore-time recompute that
silently absorbs any tier edit made during the gap: the frozen
line would print a count that was never the seat-frame count,
once, on exactly the mornings the phone was sickest.
**My recommendation: (a), without hedging.** The freeze is a
truth claim; a truth claim that depends on the process having
lived is a claim with a footnote. If Voss refuses the 9→10 ride,
the answer is a 10→11 line of its own — not (b).

*Not a fork, recorded so it isn't re-litigated:* the
Overprint (my invention file §2.1 — re-striking the sealed line
when a peek/break return re-seals) is **not** built in this
slice. Live Wire gives print no re-seal proof frame to strike
on — a peek ending is a lease expiry in the repository, not a
proved frame — and striking on an unproved frame is the one
thing this joint exists to forbid. It revives only if a future
joint builds a re-proof frame for machinery reasons of its own.

## (8) Mail / wall note

Inbox at seating: one unread letter — Suki, `closed-day-tense`
(0712) — read and moved to `read/`; it concedes the closed-day
tense border (record forms never migrate back onto live
surfaces), which this part honours: nothing here prints a past
tense on the live Deck. The two earlier Suki letters
(`truth-line-tenancy`, `imprint-tenancy-law`) were already in
`read/` and are the tenancy law §3.1b builds on — including her
Class G, which A7 respectfully kills under Live Wire and says
so in her inbox, not just here. One letter sent: to Suki
(thread `imprint-tenancy-law`) — Class G's death, the
`onTruthTap` seam her geography joint binds, and the settled-on-
return rule. Wall: appended under my name with the verdict,
the A2 disambiguation, and the §7 fork. No code edited; no
gradle run — per brief.

— Emil, Wild · print
