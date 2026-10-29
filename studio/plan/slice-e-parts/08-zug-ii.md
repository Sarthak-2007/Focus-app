# SLICE E — ZUG-II (Improvers, the slide): E3's SHOT MACHINERY

**Seat:** Zug-II, Improvers — slide/valve. Seated by Ilya for the
SLICE E BUILD-READY wave.
**Joint owned:** E3's machinery only — `SealShotState`, the SLED
ghost through Settle, the `effectiveNight` deferral, input death,
and the spectacle audit. The shot's *values* are Wren's
(`slice-de-parts/wren.md` §3.3), the exit's *values* are Mara's
(`slice-de-parts/mara.md` joint b), t=0 and the `ArmPhase` type are
Opal's (`slice-de-parts/opal.md` §3.1–3.2). This file is how those
values are carried by real code.
**Base:** trunk as it stands today —
`Focus-app/app/src/main/java/com/example/…` (`TactileDeckScreen.kt`
534 lines, `BlockitCoreViews.kt` 1,419 lines, `MainActivity.kt`,
`ui/theme/Theme.kt`). Read-only; no gradle run.
**Inbox:** empty at start (10 letters, all already in `read/`).

> **⚠ BASE-SHIFT WARNING — read first.** Marlowe's lane is
> rewriting the slide view RIGHT NOW (Slice D steps 2+3) in
> `builds/slide`. When Ren merges it, the trunk's
> `BlockitCoreViews.kt` slide region changes under this spec.
> Every insertion point inside that region is flagged
> **⚠ RE-PIN AFTER SLIDE MERGE** below: the E builder must
> re-locate it in the merged view and re-verify the surrounding
> semantics (gate threshold, animator names, field names) before
> trusting any line number here. Unflagged sites are in
> `TactileDeckScreen.kt` / `MainActivity.kt` / `Theme.kt` /
> the wheel region of `BlockitCoreViews.kt` and stand as pinned —
> except that the whole of `BlockitCoreViews.kt` shifts line
> numbers in the merge, so even wheel-region *line numbers* in
> that file must be re-grepped (their *semantics* are not in
> Marlowe's lane; the slide region's semantics are).

**Slide region, defined for the flags:** `SlideToStartView`,
trunk `BlockitCoreViews.kt:1046–1229` (class through
`animateSpringBack`), plus its host call site in the Deck,
`TactileDeckScreen.kt:486–505` — the Deck file is not being
rewritten, but that call site's *counterpart semantics* (what the
view accepts) are Marlowe's, so it is flagged too.

---

## 0 — VERDICTS

1. **SealShotState — BUILDABLE as specced in §1.** One
   `Animatable` + derived signals, remembered in
   `TactileDeckScreen`, keyed by `sessionId`, deriving every
   sampled value from Opal's `t0Mono`. No second clock anywhere
   in Slice E.
2. **Sled ghost — the frozen fraction is 1.00, by definition of
   t=0, not "wherever the integrator left it"** (§2). t=0's max()
   includes the pawl-seated frame; the seat completes before t=0
   exists. The spec captures the fraction at t=0, asserts 1f, and
   names the one lawful exception (Proving Creep finishing the
   last step *before* t=0, never after).
3. **effectiveNight deferral — Wren's amendment A3 is necessary
   but NOT sufficient as filed.** The Deck-local split at
   `TactileDeckScreen.kt:119` does not govern the screen, because
   the boolean is already polluted *upstream twice*: 
   `MainActivity.kt:232` ORs the session into the value it passes
   as `isDarkTheme` (Deck call site `:313`), and
   `MyApplicationTheme` flips the whole `MaterialTheme` scheme at
   `Theme.kt:104–108` off `MainActivity.kt:106`. Every
   `scheme.*` consumer on the Deck (panel surface `:191`, progress
   fill `:346`, task line, chips) rides the *scheme*, not `:119`.
   §3 names the two replacement signals and every consumer; the
   ground step at t=400 must be implemented at the theme/caller
   level or the panel steps at t≈0 anyway and Wren's lying second
   step ships. One fork (E3-Z1, clock-owner inversion).
4. **Input death — 8 surfaces enumerated in §4**, one more than
   the brief's list: the **FLOW key** (`TactileDeckScreen.kt:310`)
   hides inside the wheel branch (`:273`) that ghost retention
   keeps composed — under a naive ghost build it stays *live*
   through the whole shot. Mechanisms differ by surface kind
   (view gate / handler removal / consume-and-drop); the law is
   one: **a dead tap is dropped at its source, never stored,
   never replayed** — and queued *motion* (coast, spring-back,
   a pending hub commit) is a queued tap and dies the same way.
5. **Spectacle audit — ZERO freezes confirmed, ledger stays at
   4** (§5), argued phase by phase against today's render path.
   Two near-misses are argued clear in the open (the cap's 50ms
   tone staircase; the frozen-blur fallback). One implementation
   form is forbidden by name because it *would* spend a freeze:
   pausing `vertigoT` to stage the t=400 step.

**Price: ≈410 gross / ≈365 net** production lines for E3's
machinery (§6) — ≈15 net over BUILD-READY's ≈350 for E3, the
delta itemised: the upstream scheme split (§3, missed by every
prior seat) and the FLOW-key surface (§4).

---

## 1 — SealShotState: the one clock

### 1.1 The contract it derives from

t=0 ≡ Opal's PROOF-SEAL FRAME: `t0 = max(last link
CONFIRMED-SHUT, latch + 210ms, pawl-seated frame)`, delivered to
the Deck as `ArmPhase.SEALED_RUNNING(sessionId, t0Mono)`
(`opal.md` §3.1; the type is new Slice-D code — grep-verified
absent from today's trunk: no `t0Mono`, no `PROVING`, no
`Animatable` anywhere under `ui/`). `t0Mono` is sampled by the VM
controller on the PROVING → SEALED-RUNNING edge, on
`SystemClock.uptimeMillis()` — the spec binds that clock by name:
every consumer below computes elapsed as
`uptimeMillis() − t0Mono`; no `System.currentTimeMillis()`, no
frame-count, no locally-started tween clock may substitute.
This is the **shared clock contract**: E3's phases, E5b's orange
exit, the t=400 ground step, and E6's later anchors that key off
the seal all read offsets from this one sample. (E4's E-clock is
a *different* zero on the same monotonic clock — it does not
read this state; §1.4.)

### 1.2 Fields, owner, lifetime — exact

New holder class, filed in `TactileDeckScreen.kt` beside the
composable (insertion after the `:118–119` derivations —
**not** in the slide region; pin stands):

```kotlin
class SealShotState(val sessionId: Int, val t0Mono: Long) {
    val vertigoT = Animatable(0f)                 // the ONE Animatable
    @Volatile var started: Boolean = false        // launch guard, §1.3
    fun elapsedMs(nowMono: Long): Long = (nowMono - t0Mono).coerceAtLeast(0)
    fun fraction(nowMono: Long): Float =
        (elapsedMs(nowMono) / 1050f).coerceIn(0f, 1f)
    fun phase(nowMono: Long): ShotPhase = when {
        elapsedMs(nowMono) < 180  -> ShotPhase.DEPARTURE
        elapsedMs(nowMono) < 780  -> ShotPhase.RECESSION
        elapsedMs(nowMono) < 1050 -> ShotPhase.SETTLE
        else                      -> ShotPhase.DONE
    }
    fun exitPhase(nowMono: Long): Int =           // Mara E5b, §1.4
        (elapsedMs(nowMono) / 50).toInt().coerceIn(0, 3)
    fun heroLanded(nowMono: Long): Boolean = started
    fun groundStepped(nowMono: Long): Boolean = elapsedMs(nowMono) >= 400
}
```

- **Owner:** `val shot = remember(sessionId) { SealShotState(sessionId, t0Mono) }`
  in `TactileDeckScreen` — remembered, never `rememberSaveable`
  (a restored shot is a replayed shot; §1.5). It is **not** in
  the ViewModel (the VM owns `t0Mono` and the `ArmPhase` flow,
  nothing visual), **not** in either AndroidView (views receive
  pushed values in `update{}`, as today at `:281`/`:494`).
- **Lifetime:** born on the first composition that observes
  `SEALED_RUNNING(sessionId, t0Mono)`; lives for the whole
  session — past t=1050 its `vertigoT` rests at 1f and its
  derived signals (`groundStepped = true`, ghost settle values)
  are the running Deck's standing state. Dies when `sessionId`
  changes or the session ends (E-clock territory: the finish
  never samples it backwards — Wren §4.4's no-reverse-Vertigo law
  is enforced structurally by this lifetime: at E0 the state is
  already at rest and nothing rewinds it).
- **The Animatable's job is narrow:** `vertigoT.value` is the
  per-frame *driver* the composition reads for transforms/blur.
  It is launched once (§1.3) with `tween(1050, easing =
  CubicBezierEasing(0.5f, 0f, 0.85f, 0.35f))` (transforms); blur
  samples the *same* `vertigoT.value` re-eased through
  `CubicBezierEasing(0.32f, 0f, 0.18f, 1f)` at the read site —
  two pure functions of one value, so picture and defocus cannot
  desync (Wren §3.2, carried). Both easings are new constants in
  `MonolithCurves.kt` (trunk file is 47 lines, durations strictly
  {180, 260, 280, 420, 520} — 1050 is a new constant there, priced
  in §6).

### 1.3 Derive, don't replay — the launch rule

`LaunchedEffect(shot.sessionId)` fires the launch **once**:

- If `fraction(now) == 0f` (the normal case — the effect lands
  within a frame or two of t=0): `vertigoT.animateTo(1f, tween(1050, …))`.
- If `0f < fraction(now) < 1f` (recomposition/process return
  mid-shot): `vertigoT.snapTo(fraction(now))`, then
  `animateTo(1f, tween((1050 − elapsed).toInt(), …))`. The shot
  resumes at its true elapsed position in one frame. **No
  catch-up travel, ever.**
- If `fraction(now) >= 1f`, or the session was *already running
  at first composition* (no SEALED edge observed in this
  process — Deck opened mid-session, process restored):
  `snapTo(1f)`, `started = true`, **no animation at all**. A
  shot nobody was present for does not perform itself later.
  This case is decided by a constructor flag
  (`preExisting: Boolean` — true when the first observed
  `ArmPhase` for this composition is already `SEALED_RUNNING`
  with `uptimeMillis() − t0Mono >= 1050`, or when `activeSession`
  is non-null on the Deck's very first frame): the builder must
  not key "pre-existing" off `isSessionRunning` alone, or every
  normal seal (which also flips `isSessionRunning`) risks
  classifying itself pre-existing on a slow first frame. The
  distinguishing fact is *elapsed at first observation*, nothing
  else.
- Reduced motion: `vertigoT` still runs (the signals must step),
  but readers substitute Wren's filed form — 200ms peripheral
  fade, readout static, sync points collapse to t=0. The state
  class is unchanged; only the Deck's read sites branch.

### 1.4 Who reads it — the reader table

| Reader | Reads | When |
|---|---|---|
| E3 Departure/Recession/Settle wiring (Deck) | `vertigoT.value` (transform easing + blur re-ease), `phase()` | every frame, t=0→1050 |
| Hero glyph tier (Deck `:250–252` via §3) | `heroLanded()` | from t=0 |
| Ground/panel/scheme (Deck + `MainActivity`/`Theme` via §3) | `groundStepped()` | steps once at t=400 |
| **E5b orange exit (Mara)** | `exitPhase()` — steps at elapsed 0/50/100/150, terminal from 150, ink guaranteed by 200 | **runs inside Departure** (0–180) and closes 20ms into Recession; its phase is *this state's* phase function, never a Choreographer callback of its own (Mara §5's standalone fallback dies the day this lands, as she filed) |
| E5a D6 hub core (Mara) | **nothing** — the hub rides the wheel's transform only; its light reads press depth, never the shot clock (Mara A4). Named here so no builder wires it. | — |
| E6 sled return (Fen) | **nothing from this state** — E6 anchors to `ReceiptState` (receipt +1000ms), a different clock event. The ghost's *existence* (§2) is E3's; its departure is E6's. | — |
| E4 finish | **nothing** — no reverse Vertigo (Wren §4.4). | — |

### 1.5 Risks (state)

- **R-S1 · Double launch.** `LaunchedEffect` re-entry on
  recomposition churn near the seal frame could restart the
  tween from a stale value. The `started` guard + keying the
  effect on `sessionId` (not on `isSessionRunning`) is the whole
  defence; acceptance test T2 (§7) hammers it.
- **R-S2 · Clock mismatch.** If D's controller samples `t0Mono`
  on `elapsedRealtime` while the Deck reads `uptimeMillis`, the
  shot is offset by deep-sleep time — invisible in a demo,
  wrong after the phone sleeps mid-arm. The clock is named in
  §1.1 precisely so the D builder and E builder cannot drift;
  T1 asserts it by construction (one constant, one file).
- **R-S3 · AndroidView blur jank** (Wren risk 1, inherited): if
  the device pass freezes wheel blur at its Departure value, the
  state is untouched — blur is a read site, §5 clears the audit
  consequence.

---

## 2 — The SLED ghost through Settle (F17, ratified)

### 2.1 Today's deletion sites — exact

At the seal frame today the shot's subjects *cease to exist*:

- **Wheel:** `TactileDeckScreen.kt:273` — `if (!isSessionRunning) {
  AndroidView(RotaryWheelView) … } else {` progress track
  (`:337–365`, 6dp track + % at `:346`/`:357–363`) `}`.
- **Sled:** `TactileDeckScreen.kt:486` — `if (!isSessionRunning) {
  AndroidView(SlideToStartView) }` (`:487–505`) `else {`
  grace-abort chip (`:512–521`) / emergency chip (`:523–532`) `}`.
- The slide view also *un-seats itself* on success today:
  `BlockitCoreViews.kt:1177–1186` **⚠ RE-PIN AFTER SLIDE MERGE**
  — on commit it sets `dragFraction = 1f`, plays the seal, then
  `animateSpringBack(0f)` springs the cap home. And its
  `isArmedState` setter (`:1064–1072` **⚠ RE-PIN**) resets
  `dragFraction = 0f` on any change — the Deck's `update{}`
  pushes `isArmedState = false` on every recomposition
  (`TactileDeckScreen.kt:497` **⚠ RE-PIN** — host side of the
  slide contract).

### 2.2 The composition change at the seal frame — exact

The change is **not** "keep the else-branch from firing." It is
a restructure of both conditionals into a three-state host:

1. **Hoist both AndroidViews out of the `isSessionRunning`
   conditional entirely.** Each `AndroidView` sits at one fixed
   composition slot for the Deck's whole life; idle / proving /
   sealed differ only in what `update{}` pushes. This is
   load-bearing, not stylistic: an `AndroidView` that leaves and
   re-enters composition is a *new View object* — a sled that
   "stays" by being recomposed in a different branch is a new
   sled at `dragFraction = 0f` standing at the start of the
   track, i.e. a teleport, the exact failure F17 exists to kill.
2. **Wheel slot:** while sealed, `update{}` pushes
   `isLockedSession = true` (§4) and the Deck wraps the slot in
   the shot's `graphicsLayer` (scale / translationY / alpha /
   blur per Wren §3.3, sampled from `vertigoT`). The progress
   track branch (`:337–365`) is **not composed during the shot**;
   its post-Settle fate is the running-Deck owner's, as Wren
   filed — but note for that owner: at Settle the wheel ghost
   *is* the running rendering at 0.80 / y+20dp / α0.50 / blur
   6dp, so composing the track at t=1050 alongside it renders
   elapsed twice on one panel. Flagged, not priced here.
3. **Sled slot (pinned commit bar):** while sealed, `update{}`
   pushes the new sealed-ghost state (§2.3) and nothing else.
   The grace / emergency chips (`:506–532`) **leave this slot** —
   they cannot share it with the ghost. Their new home: the
   session-utilities row (`:454–472`, DESK CLOCK / HELD),
   composed at Settle (t=1050) by CUT 0 with that row, per Wren
   A4 — grace keeps ≥8.95s of usable window (its 10s clock starts
   at t=0, its control arrives at 1050). **Fork E3-Z2** (§8):
   if the utilities row cannot take a third/fourth chip at
   Kato's room sum (821dp), the fallback is a second row directly
   under it, also at Settle — under no ruling do the chips evict
   the ghost or compose during 0–1050.

### 2.3 The frozen fraction — 1.00, and why

**The sled sits at the seat: frozen fraction = 1.00 exactly.**

Argument, not preference: t=0 = `max(last CONFIRMED-SHUT,
latch + 210ms, pawl-seated frame)` (MASTER-PLAN §2.6). The
pawl seats only on a seated sled — §2.6's Proving Creep walks
the sled home "in uneased one-frame steps, one per CONFIRMED;
the pawl drop is the last step." So by the frame t=0 exists,
every creep step has run and the sled is *at* the seat. A
fraction < 1.00 at t=0 would mean t=0 fired before the seat —
a contradiction of t=0's own definition. The trunk agrees in
its own idiom: its commit path sets `dragFraction = 1f`
outright before firing (`BlockitCoreViews.kt:1178` **⚠ RE-PIN**).

Spec, exact:

- At the SEALED edge, the Deck's `update{}` pushes
  `view.sealGhost()` — a new internal method on
  `SlideToStartView` **⚠ RE-PIN AFTER SLIDE MERGE** (insert
  beside the `isArmedState` setter, trunk `:1064–1072`):
  cancels `springBackAnimator` (trunk field `:1078`), sets
  `isDraggingThumb = false`, sets `dragFraction = 1f`, sets the
  sealed gate (§4), `invalidate()`. One frame, no animation:
  if the captured fraction was somehow 0.97, this *is* the last
  creep step, executed at t=0 as part of the seat, not a
  post-t0 motion — and an assertion in the build's test (T4)
  logs any capture ≠ 1f as a D-lane defect, not an E licence.
- The spring-home on success (`animateSpringBack(0f)`, trunk
  `:1186` **⚠ RE-PIN**) must be dead on the commit path before
  E3 builds on the view — it is Slice D's seat law (Mara R2
  sequencing note); if D's merge has not killed it, E3's ghost
  springs home orange *while exiting* and §5's audit is void.
  Named as a build-order dependency in §8.
- `isArmedState`'s fraction-reset (trunk `:1064–1072` **⚠
  RE-PIN**) must never fire while ghosted: either D's rewrite
  removes the reset, or `sealGhost()` sets a guard the setter
  checks first. The Deck's `update{}` push of
  `isArmedState = false` (`:497` **⚠ RE-PIN**) is a no-op only
  while the value never changes — the E builder re-verifies
  this in the merged view; a merged setter with side effects on
  *any* push breaks the ghost silently.
- **What the ghost renders:** cap at the seat (fraction 1.00 ⇒
  label/badge alpha = `(1 − 1.3·1.0).coerceIn(0,1)` = 0 already,
  by the shipped law at trunk `:1111` **⚠ RE-PIN** — the ghost
  is cap + track + trail, no words), cap/trail tone driven by
  Mara's exit phase (§1.4) to terminal ink at t=150, trail at
  α55 terminal over the track. It does not move, shrink, or
  fade through Settle or the session; it leaves only under E6
  (powered return at receipt +1000ms), whose rest contract
  replaces the trunk `isArmedState` hard-reset — E6's joint,
  but the ghost's fraction field is the field E6's contract
  writes, so the name `dragFraction` and its 1f seat value are
  part of this spec's surface.

---

## 3 — effectiveNight deferral: two signals, every consumer

### 3.1 The two signals that replace the one boolean

Trunk: `TactileDeckScreen.kt:119` —
`val effectiveNight = isDarkTheme || isSessionRunning` — flips
in the recomposition that first observes the session row, at no
chosen time, for every consumer at once. Replacement, exact:

```kotlin
// glyph family — lands at t=0 with the hero content cut
val heroNight  = isDarkTheme || (shot?.heroLanded(now) ?: isSessionRunning)
// ground family — steps ONCE at t=400, driven by the shot clock
val groundNight = isDarkTheme || (shot?.groundStepped(now) ?: isSessionRunning)
```

The `?: isSessionRunning` tails are the pre-existing-session
case (§1.3): no shot in this process ⇒ both families night
immediately, no deferral theatre. In a *user's own dark theme*
(`isDarkTheme = true`) both are already true and the deferral
is a no-op — the amendment only governs the session-forced
night, which is the only night the seal *causes*.

### 3.2 Every consumer site — grep-complete over the trunk Deck

`grep effectiveNight` in `TactileDeckScreen.kt` returns exactly
7 read sites. Split:

| Site | Consumer | Family | Steps |
|---|---|---|---|
| `:216` | eyebrow-left `TextStyle.shadow` (transparent at night) | **ground** | t=400 |
| `:224` | eyebrow-right shadow (same construction) | **ground** | t=400 |
| `:250` | `heroGlyph` = night `#EDE3D8` / day `scheme.onBackground` | **hero** | t=0 |
| `:251` | `heroShadowGhost` `#80000000` / `#99D8D1CB` | **hero** | t=0 |
| `:252` | `heroCatchGhost` `#594A423C` / `#B3FAF6F0` | **hero** | t=0 |
| `:281` | `view.isNightMode = effectiveNight` — wheel `AndroidView` update | **ground** | t=400 |
| `:494` | `view.isNightMode = effectiveNight` — slide `AndroidView` update **⚠ RE-PIN** (host side of slide contract) | **ground** | t=400 |

Rulings inside the table, so the builder doesn't re-litigate:
the eyebrows sit *on the panel* — they are ground, not glyph;
the hero trio is one object and moves together at t=0 (a glyph
that lands with day ghosts under it is two heroes for 400ms);
the wheel's `isNightMode` drives its drawn tones incl. Mara's
D6 night peak (8%) — light follows the ground step, and the
hub core is *under* the wheel transform either way; the sled's
`isNightMode` at `:494` ⚠ steps at t=400 **and is not the exit's
mechanism** — Mara's carriers read the shaded-accent resolver
by `exitPhase()`, so the sled's accent cannot brighten at any
step regardless of when its mode flag flips (this is what makes
F23 hold in code, not just in print).

### 3.3 The upstream pollution — the part A3 missed

`:119` is the *second* OR. The first is in the caller:

- `MainActivity.kt:232` — `val effectiveNight = darkTheme ||
  (activeSession?.isActive == true)`, passed as
  `isDarkTheme = effectiveNight` to the Deck (`:313`), to
  Shield (`:361`), Tasks (`:392`), Vault (`:422`), Guard
  (`:437`), the header (`:259`), and the nav view
  (`view.isNightMode = effectiveNight`, `:491`).
- `MainActivity.kt:103–106` → `Theme.kt:104–108` —
  `MyApplicationTheme(darkTheme, isFocusSessionActive =
  activeSession?.isActive == true)` selects
  `SealedVaultNightColorScheme` for the **whole app** on the
  same frame. Every `scheme.*` read on the Deck — background
  `:171`, panel surface/border `:191–192`, `onSurfaceVariant`
  `:215/:223/:329/:357/:363/:418`, FLOW key `:308–322`,
  progress track/fill `:339/:346`, task line `:386–442` incl.
  the B3 numerals at `:436`, utilities chips `:454+`, the
  grace/emergency chips — steps when the *scheme* steps, and
  no Deck-local boolean can split them.

**Consequence, stated plainly:** a builder who splits only
`:119` ships the ground step at t≈0 (via the scheme) *plus*
Wren's t=400 step on the 7 explicit sites — two steps, the
second one lying, exactly the failure A3 was written to
prevent. The deferral must therefore be built where the flip
actually lives:

1. `MainActivity.kt:313`: the Deck receives the **raw**
   `darkTheme`, not `:232`'s OR. (Parameter keeps its name
   `isDarkTheme`; its meaning becomes honest.)
2. `Theme.kt:104–108` / `MainActivity.kt:106`: the scheme's
   session term becomes the **ground signal**:
   `isFocusSessionActive = groundNight`, where `groundNight`
   at app level = `darkTheme || sessionGroundStepped`.
3. `MainActivity.kt:232` splits: header (`:259`) and nav
   (`:491`) take `groundNight` (both are in the shot's frame —
   chrome is static and sharp, but it is *in the room* whose
   ground steps at 400; a header that flips at t=0 while the
   panel flips at 400 is a visible seam across one screen).
   Shield/Tasks/Vault/Guard (`:361/:392/:422/:437`) keep the
   immediate session-night — they are off-screen during the
   shot, and a user navigating to them mid-session (post-input-
   death ruling, Band's joint) should find them already night.
   Their term stays `darkTheme || isActive`.

**Fork E3-Z1 — the clock-owner inversion (§8).** The theme is
an *ancestor* of the Deck in composition; a `remember`ed state
inside `TactileDeckScreen` cannot feed it in the same pass.
Two lawful builds: **(a) VM mirror (recommended):** the VM
controller already holds `t0Mono`; on the SEALED edge it exposes
`StateFlow<ShotGround>` (`NOT_SEALED / SEALED_PRE_GROUND /
GROUNDED`) flipping at `t0Mono + 400` via its own scope —
`MainActivity` collects it; the Deck's `SealShotState` remains
the visual clock and derives its `groundStepped` from the same
`t0Mono + 400` arithmetic, so the two cannot disagree by more
than a frame, and T6 (§7) asserts they don't. Cost ≈15 lines in
the VM (counted in §6). **(b) Hoist:** `SealShotState` is
remembered in `MainActivity` and passed into the Deck — one
clock, no mirror, but the state's owner moves off the Deck,
against the E3 entry's letter ("owned in TactileDeckScreen").
(a) keeps the letter and the truth; (b) is cleaner code. Ren
rules; the builder does not improvise a third route (e.g. a
`CompositionLocal` written by the child and read by the parent
— that ordering does not exist).

### 3.4 What does NOT split

`isSessionRunning` (`:118`) itself is untouched — content
branches (hero form `:236`, finish pair, progress math
`:144–153`) cut at t=0 as Wren specced; only *tone* defers.
`blockedCount`, `displaySeconds`, the eyebrow *words* (`:207–209`)
are facts, not tones; they change when the facts change.

---

## 4 — INPUT DEATH, enumerated (t=0 → the finish)

**The law (one sentence, binding on every surface):** from the
t=0 frame, a tap on a dead surface is *dropped at its source* —
consumed and discarded, or never received because no handler
exists — and **nothing in the system stores it**: no pending
navigation, no deferred callback, no `update{}` lambda that
fires on a later recomposition, no animator or physics that
carries a pre-t0 gesture's intent past t=0. Queued motion is a
queued tap.

Death is **by state, for the session's life**, not for 1050ms —
the shot ends at Settle; the seal doesn't. Surfaces revive only
on their own lawful events (utilities at Settle, §4.9; everything
else at session end / E6 rest). Per surface:

### 4.1 Slide — view gate, ghost stays composed
`SlideToStartView.onTouchEvent`, trunk `:1153–1196`
**⚠ RE-PIN AFTER SLIDE MERGE.** Mechanism: **internal gate,
not removal** (the ghost must stay visible, §2). New
`sealedGate: Boolean` set by `sealGhost()` (§2.3); first line of
`onTouchEvent`: `if (sealedGate) return true` — **consume and
drop** (`true`, not `false`: `false` hands the event to the
parent chain, and a dead sled must not become a scroll or a
fall-through tap on whatever sits under the pinned bar).
`sealGhost()` also cancels `springBackAnimator` and clears
`isDraggingThumb` (trunk `:1076–1078` **⚠ RE-PIN**) — a finger
mid-drag at the latch frame leaves no animator running.

### 4.2 Wheel — view gate that already exists, finally fed
`RotaryWheelView.onTouchEvent`, trunk `:753–754`:
`if (isLockedSession) return false` — the gate exists; today
it is never fed during a session because the wheel is deleted
at `:273` and its `update{}` hardcodes
`view.isLockedSession = false` (`TactileDeckScreen.kt:285`).
Under ghost retention: `update{}` pushes
`isLockedSession = true` from the SEALED edge. Two completions
the shipped gate lacks: **(i)** the `isLockedSession` setter
(trunk `:384–391`, wheel region — line re-grep after merge,
semantics stand) must call `stopPhysics()` (trunk `:918`,
private) when flipping to `true` — a fling/coast in flight at
t=0 is queued motion and dies at t=0; the readout's count comes
from state, never from angle (Wren A4). **(ii)** return-value:
wheel's `return false` is *acceptable* here only because its
parent is the panel inside a scroll column — wait, that is the
fall-through §4.1 forbids. Ruling: change to consume-and-drop
(`return true`) while locked, so a dead-wheel drag cannot
scroll the Deck mid-shot. One-word change, named so D's lane
doesn't "fix" it back.

### 4.3 Hub — same view, separate corpse
The hub press lives in the same `onTouchEvent` (trunk
`:767–838`, wheel region). The `:754` gate covers its events,
but **state** survives the gate: a hub DOWN at t=0⁻ sets
`hubPressed = true` / `hubTarget` (trunk `:398–399`); the UP at
t=0⁺ is gated, so the press never clears — and if the gate ever
lifts mid-session (it must not, but defence in depth), that UP
commits a count from a pre-seal press: a queued tap in a field.
Spec: the `isLockedSession = true` flip also clears
`hubPressed = false; hubTarget = null` and cancels Mara's
press-depth animator if E5a has landed. Acceptance T8.

### 4.4 FLOW key — the surface nobody listed
`TactileDeckScreen.kt:296–335`, inside the wheel branch
(`:273`): the FLOW key cap, `Modifier.clickable` at `:310–313`
calling `onSelectStudyPreset(STOPWATCH_FLOW)` +
`onDialMinutesChanged(5)`. Today it dies by deletion with the
wheel. **Ghost retention resurrects it** — it sits under the
wheel in the same branch the shot keeps composed. Mechanism:
**handler removal** — from the SEALED edge the whole FLOW/finish
row is recomposed *without* its `clickable` (the row's text is
a fact rendering and may stay as scenery during the shot; at
Settle the running branch's own row — finish pair + %,
trunk `:350–365` — replaces it, which is the branch swap the
builder performs at t=1050, not t=0). Named as a fork-proof
instruction: the builder does **not** gate this with
`clickable(enabled = false)` — a disabled Compose clickable
still occupies the touch chain differently across versions;
absence is the spec.

### 4.5 Task line — verbs die, the line persists
`TactileDeckScreen.kt:372–452`. Wren A2b: the line persists as
the running micro-line; its verbs die at t=0. Sites:
title expand toggle (`:402–408`, conditional `clickable` —
the shipped code already patterns the exact conditional-
Modifier idiom to reuse), DONE (`:415`,
`onCompleteActiveTask`), QUEUE (`:423`,
`onOpenTaskSequencer`), sub-step rows (`:433`,
`onToggleTaskSubStep`, only composed when expanded — an
expanded list at t=0⁻ freezes expanded as scenery; its rows
lose their handlers). Mechanism for all four: **handler
removal by state** — `if (sealed) Modifier else
Modifier.clickable { … }`. A DONE tap stored and fired at
session end would complete a task the man never confirmed
finishing — the law's clearest case.

### 4.6 Scroll — the panel itself
`verticalScroll(rememberScrollState())`, `TactileDeckScreen.kt:176–179`.
A scroll drag during 0–1050 drags the transforming panel —
motion the shot didn't author, on the shot's own subject.
Mechanism: `Modifier.verticalScroll(state, enabled =
!shotActive)` for **t=0→1050 only**; scroll revives at Settle
(the running Deck is a real screen again; its utilities may
sit below the fold on short rooms). `enabled = false` on a
scroll modifier stores nothing — a fling in flight at t=0 is
cancelled by the modifier, not queued. Named explicitly
because no prior seat listed it.

### 4.7 Nav — named, not proven here
`MainActivity.kt:485–505`: `MonolithBottomNavView` `update{}`
pushes `onTabSelected → viewModel.navigateTo(dest)`; today
nothing absorbs it mid-session (`AnimatedContent` at `:284`
switches freely). **Band owns this surface's mechanism and its
proof; this spec names the requirement his part must meet:**
from t=0 for the session's life, a nav tap is consumed at the
view (filed grammar: detent tick, tries and stays, 4ms @ 0.2 —
Wren A4(5)) and `navigateTo` is **not invoked** — not invoked
later, not debounced, not stored as a pending destination to
honour at Settle. If Band's mechanism instead gates in
`navigateTo` itself, the same law binds there: drop, don't
queue. E3's acceptance T7 tests the *observable* (destination
unchanged after taps at t=100/500/1000 and after Settle), whoever
owns the gate.

### 4.8 Header chrome — named, extra surface
`MonolithPullDownHeader` callbacks (`MainActivity.kt:259–267`:
drawer toggle, Guard navigation, theme toggle) are live taps
in the shot's frame, above the Deck. The drawer opening over
Departure is a door in the shot. Named here, **not priced**:
owner is chrome/Band territory alongside nav; the requirement
is the same law (absorb during 0–1050 at minimum; the theme
toggle during a session is additionally a tone event §3 owns —
it must step the *ground* family only when the shot is not
running, or it fights the deferral). Flagged for Ilya to seat
explicitly rather than let it ship by omission.

### 4.9 What is alive — the boundary, stated
Nothing on the Deck, t=0→1050 (Wren A4, carried). At Settle
(t=1050), by CUT 0: the utilities row (`:454–472` DESK CLOCK /
HELD) and the relocated grace/emergency chips (§2.2). The
emergency path's 60s-hold valve composes there — a session
1 second old has no door yet; that cost is Wren's, stated in
her file, unchanged by this machinery. System back during the
shot: leaves the app (Android's law, not ours) — it does not
navigate inside the app, and on return §1.3's derive-don't-
replay governs what he sees.

---

## 5 — SPECTACLE AUDIT: zero freezes, re-argued on today's render path

S2's definition (MASTER-PLAN §2.6, closed budget): a freeze is
a **hit-stop — state and picture intentionally held for effect,
with input returning after**. The ledger stands at exactly
four: **slide commit 40ms · final sub-step 50ms · DONE landing
40ms · bank catch 40ms**. A fifth may be proposed only by naming
which of the four dies. E3's claim, re-argued phase by phase
against what the trunk actually renders:

- **Pre-t0 (PROVING, for completeness):** not E3's window, but
  the audit starts where a hidden freeze would hide. The
  picture is a *seated sled walking* — Proving Creep steps the
  sled one frame per CONFIRMED (§2.6): motion, stepped, not
  held. The pawl drop is a step, not a stop. No freeze.
- **Departure (0–180):** *State* is not held — the hero content
  hard-cuts at t=0 (trunk branch `:236` switches
  `heroAnnotated` to the running readout) and the readout
  counts from t=0. *Picture* is not held — wheel Δscale 0.015,
  Δy 3dp, Δblur 0.7dp across the phase under
  `CubicBezier(0.5, 0, 0.85, 0.35)`. Honest precision: that
  easing's derivative at the single instant t=0 is zero
  (P1 = (0.5, 0)) — a zero-velocity *instant* at a motion's
  start is not a hold; on the open interval (0, 180) velocity
  is strictly positive and every frame differs from the last.
  *Input* does not "return after" — it died by state change,
  permanently (§4). Fails all three clauses. **Not a freeze.**
- **The orange exit inside Departure (0–200, E5b):** the
  audit's hardest case, argued head-on: the cap's *tone* holds
  for 50ms at a time (steps at 0/50/100/150). Is a held tone a
  held picture? The cap's picture *changes* at every step; the
  cap is a seated object whose only lawful change is tone —
  between steps it is exactly as still as it is at t=300, at
  t=3000, for the whole session, and nobody calls a seated sled
  a freeze. Meanwhile the shot's picture (wheel, task line,
  transforms) moves uninterrupted across every step boundary.
  A staircase is not a hit-stop. **Not a freeze** — but named:
  if the studio ever rules per-element tone holds as freezes,
  this is the element, and the price would be one of the four.
  My ruling stands against it: the definition says *state and
  picture*, and neither is held.
- **Recession (180–780):** the fastest motion in the shot;
  nothing to argue. The one channel that *can* hold is blur,
  under Wren's device fallback (wheel blur frozen at its
  Departure 0.7dp if live blur janks): one static channel on an
  element simultaneously scaling, translating, and fading is
  not a held picture — the fallback is a *quality* reduction,
  not a stop. Cleared, with the condition that the fallback is
  chosen by device gate *before* the shot, never toggled
  mid-shot (a mid-shot blur snap would be a cut — lawful, but
  a different defect; the gate exists to prevent it).
- **The t=400 ground step:** a 1-frame cut *inside* continuous
  motion — transforms sample straight across it; the stepped
  surfaces (panel, scheme) are the ground, and a ground that
  changes tone in one frame while everything on it keeps moving
  is the opposite of a hold. Two implementation warnings, both
  binding: **(i)** the step must be a *value* step (signal flip,
  §3), never a pause — a builder who stages the deferral by
  sequencing `vertigoT` (tween to a mid-value, `delay(400)`,
  tween on) **spends a real freeze and breaks this audit**;
  that form is forbidden by name. **(ii)** On today's render
  path the scheme flip recomposes the whole Deck in one frame
  *while* an `AndroidView` is being blurred — a device could
  hitch that frame. A hitched frame is jank, not a hit-stop
  (intent and input-return both absent), but T9 (§7) measures
  it, because jank at t=400 on every seal would be its own
  defect even with the ledger clean.
- **Settle (780–1050):** decelerating approach; velocity
  reaches zero only *at* the endpoint, which is where motion
  ends, not where it is suspended for effect — and input does
  not return at 1050 for the shot's surfaces (§4.9: only the
  utilities are born; the shot's subjects stay dead). Fails
  the definition. The settle knock (16ms @ 0.7 at t=1050) lands
  *on* arrival; a sound is not a held picture.
- **The invariant (all 1050ms):** readout scale = 1.0
  throughout. An invariant is not a hit-stop — its content
  strikes every true second inside the shot. Wren's ruling,
  re-argued and held: if the studio rules invariants freezes,
  the price is named (one of the four dies); nobody has, and
  the readout-at-rest is the shot's entire premise (the number
  stays; the room changes).

**Verdict: E3 spends 0 freezes. The ledger stays at 4**
(slide commit 40 / final sub-step 50 / DONE 40 / bank catch
40). **No fork is filed** — both near-misses (cap tone
staircase, frozen-blur fallback) are argued clear above with
their re-opening conditions stated, which is what the audit
owes; a fork would be filed only if either argument failed,
and neither does. Cross-reference, not E3's account: E4's
HELD FRAME (1200ms of true stillness at the finish) is a
*held picture by design* — whether it is a hit-stop under S2
(input does not return after it in the relevant sense; its
skip law makes any tap end it) is E4/Wren's audit to argue,
and this file does not spend or excuse it.

---

## 6 — PRICE (net lines, production; against the trunk)

| Piece | Site(s) | Net |
|---|---|---|
| `SealShotState` + `ShotPhase` + launch effect | `TactileDeckScreen.kt` (new, after `:119`) | ≈60 |
| Easing/duration constants (Vertigo + blur curves, 1050) | `MonolithCurves.kt` | ≈10 |
| Transform/blur wiring (wheel slot, task line, panel) | Deck `:273` region, `:372` region | ≈110 |
| Ghost retention restructure (hoist both AndroidViews, three-state host, chips relocate to utilities row) | Deck `:273`, `:454`, `:486` | ≈65 |
| `sealGhost()` + sealed gate + state clears | `BlockitCoreViews.kt` slide region **⚠ RE-PIN** | ≈30 |
| effectiveNight split — Deck 7 sites (§3.2) | Deck `:216–494` | ≈15 |
| effectiveNight split — upstream (raw param `:313`, app ground signal, header/nav re-point) | `MainActivity.kt:232/259/313/491`, `Theme.kt:104` | ≈20 |
| VM ground mirror (fork E3-Z1 option a) | `MonolithViewModel.kt` / controller | ≈15 |
| Input death — Compose side (FLOW row, task-line handlers ×4, scroll switch) | Deck `:176`, `:296–452` | ≈35 |
| Input death — view side (wheel setter completions §4.2–4.3, consume-drop) | `BlockitCoreViews.kt` wheel region | ≈15 |
| Exit-phase plumbing share (E5b's reader wiring; values are Mara's ≈50) | Deck + slide region **⚠ RE-PIN** | ≈10 |
| Ring field scenery (Wren A5 — **cuttable**, not in net) | Deck, new Canvas | ≈45 gross |
| **Total** | | **≈410 gross / ≈365 net** (excl. cuttable ring field) |

Reconciliation with BUILD-READY's E3 ≈350: +≈15 net = upstream
scheme split (§3.3, ≈20, partly offset by Deck-side savings)
and the FLOW-key/scroll surfaces (§4.4/§4.6) that no prior price
carried. Nav's gate and header absorption are **not** in this
price (Band/chrome, §4.7–4.8, named-not-priced as the brief
orders for nav).

---

## 7 — ACCEPTANCE TESTS (device + Robolectric; each names its clause)

- **T1 (one clock):** grep gate — `t0Mono` is the only seal-time
  sample Slice E reads; `SystemClock.uptimeMillis` is the named
  clock at both the controller's sample and the Deck's reads.
  No `delay(` in any E3 read path.
- **T2 (derive, don't replay):** kill/relaunch mid-shot at
  elapsed ≈500ms → first recomposed frame shows the shot at
  its true position (fraction ≈0.48 ± 1 frame), no travel from
  0. Relaunch at elapsed > 1050 → no shot at all, ground
  already night, ghost at settle values.
- **T3 (pre-existing session):** open the Deck on an already-
  running session → both night signals true on frame 1, no
  animation, no 400ms step.
- **T4 (ghost fraction):** at the SEALED edge, captured sled
  fraction == 1.00f (assert; a miss files against D's seat, not
  E). Sled pixel-position at t=0, 400, 1050 identical; the same
  View instance throughout (identity check in test).
- **T5 (deletion death):** during the shot and at Settle, the
  composition contains exactly one wheel view and one sled
  view, zero progress-track (`:337` branch) during 0–1050.
- **T6 (two signals):** frame captures at t=0/399/400/401 —
  hero trio night from t=0; eyebrows, panel scheme, wheel and
  sled `isNightMode` day at 399, night at 400, exactly one
  step; VM ground mirror (§3.3a) flips within 1 frame of the
  Deck's `groundStepped`. In user dark theme: no visible step
  at any of these frames.
- **T7 (input death, observable):** taps/swipes at t=100/500/
  1000 on slide, wheel (drag + hub press), FLOW key, task line
  (title, DONE, QUEUE, an expanded sub-step), scroll, each nav
  tab, header buttons → zero state change at the time *and*
  after Settle (no queued nav, no dial change, no task
  completion, no drawer). A hub press begun at t=0⁻ and
  released at t=0⁺ commits nothing (T8's case, observed here).
- **T8 (pending-state clears):** at the SEALED edge with a hub
  press and a wheel coast synthetically in flight: `hubPressed
  == false`, `hubTarget == null`, physics stopped (angle
  constant across the next 3 frames), spring-back animator
  cancelled, sled `isDraggingThumb == false`.
- **T9 (t=400 frame cost):** frame-time trace across 10 seals
  on the target device — the t=400 recomposition frame ≤ 2×
  the shot's median frame time; a miss routes to the blur
  fallback gate (Wren risk 1), not to retiming the step.
- **T10 (exit sync):** E5b's carriers (cap, trail, one done
  numeral with steps expanded — Mara R3's case) photographed
  at t=0/50/100/150/200: all three on the same step at every
  frame; zero orange at t=200; day-seal accent never brightens
  (no `#F47442` in a day seal's frames — F23 in pixels).
- **T11 (freeze ledger):** frame-diff across the full shot —
  no run of ≥2 consecutive identical full-screen frames
  between t=0 and t=1050 (the readout's per-second strikes and
  continuous transforms make a lawful shot fail this test
  never; a hit-stop implementation fails it immediately).

---

## 8 — FORKS, DEPENDENCIES, RISKS (consolidated)

- **E3-Z1 · Clock-owner inversion** (§3.3): VM mirror (rec.) vs
  hoisted state. Blocks §3's build, nothing else. Ren rules.
- **E3-Z2 · Chip slot collision** (§2.2): grace/emergency chips
  join the utilities row at Settle (rec.) vs a second row under
  it. Kato's room sum arbitrates; the ghost is not negotiable
  in either branch.
- **Build-order dependency (not a fork):** D's slide merge must
  land the seat law (no spring-home on success, trunk `:1186`
  **⚠ RE-PIN**) before or with E3; every **⚠ RE-PIN** site in
  §§2/4/6 is re-verified against the merged view at build start.
  If the merged view renames `dragFraction` / the animators,
  §2.3's method is re-sited by semantics (the field the seat
  writes; the animator that returns it), and the rename is
  reported to Ilya the same day — E6's rest contract writes the
  same field.
- **R-E1 · Two ground flips within a frame** (§3.3a mirror):
  bounded by T6's 1-frame tolerance; a miss means the mirror
  became a second clock — the fix is hoisting (option b), not
  tuning.
- **R-E2 · The `isArmedState` push** (`:497` **⚠ RE-PIN**): if
  Marlowe's merged setter gains push side-effects, the ghost
  resets mid-session with no error anywhere. T4's identity +
  fraction assertions across a forced recomposition are the
  tripwire; the E builder runs them against the merged view
  *before* building the shot on it.

— Zug-II, Improvers · the slide
